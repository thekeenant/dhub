package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Byteable;
import com.keenant.dhub.zwave.frame.DataFrame;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * This does the talking with the USB controllers.
 *
 * - It runs on its own thread and reads/writes to the serial port.
 * - Grabs the latest queued transactions from the controller, sends data frames as necessary.
 * - Listens to the port to find new status frames or data frames.
 * - Reports data frames that are not part of a transaction to the controller.
 */
public class Transceiver implements Runnable {
    private final Controller controller;
    private final SerialPort port;
    private final Logger log;

    private Thread thread;
    private boolean notifyStop;

    public Transceiver(Controller controller, SerialPort port) {
        this.controller = controller;
        this.port = port;
        this.log = Logging.getLogger(port.getSystemPortName());
    }

    /**
     * @return If the transceiver thread is currently running.
     */
    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    /**
     * Open the serial port and start the thread to write/read.
     * @throws IllegalStateException If we are already running.
     */
    public void start() throws IllegalStateException {
        if (isAlive()) {
            throw new IllegalStateException("Transceiver already started.");
        }

        // Setup the port properly...
        port.setBaudRate(115200);
        port.setParity(0);
        port.setNumDataBits(8);
        port.setNumStopBits(1);

        // Open it up
        port.openPort();

        thread = new Thread(this);
        thread.start();
    }

    /**
     * Notifies the running thread to stop reading and writing.
     */
    public void stop() {
        notifyStop = true;
        thread = null;
    }

    /**
     * @return All available bytes from the port as a raw byte array.
     */
    private byte[] readRaw() {
        if (port.bytesAvailable() <= 0) {
            return new byte[0];
        }
        byte[] bites = new byte[port.bytesAvailable()];
        port.readBytes(bites, bites.length);
        return bites;
    }

    /**
     * @return All available bytes from the port.
     */
    private ByteList read() {
        return new ByteList(readRaw());
    }

    /**
     * Write data to the port.
     * @param byteable The data to write to the port. It is converted
     *                 to bytes via #{@link Byteable#toBytes()}.
     */
    public void write(Byteable byteable) {
        log.log(Level.DEBUG, "Writing... " + byteable.toBytes());
        log.log(Level.DEV, "Writing... " + byteable);

        byte[] arr = byteable.toByteArray();
        port.writeBytes(arr, arr.length);
    }

    /**
     * Grab the current transaction from the device and write any
     * outbound frames that the transaction has queued to send.
     * @param txn T The last acquired transaction by the transceiver.
     * @return The current transaction, acquired from controller.
     */
    private Optional<Transaction> updateTransaction(Transaction txn) {
        if (txn == null) {
            txn = controller.updateCurrent().orElse(null);
        }

        while (txn != null && !txn.getOutboundQueue().isEmpty()) {
            write(txn.getOutboundQueue().poll());
            sleep(10);
        }

        return Optional.ofNullable(txn);
    }

    /**
     * Read and ignore any and all available data from the port.
     */
    private void dump() {
        while (true) {
            read();

            if (port.bytesAvailable() > 0) {
                sleep(100);
            }
            else {
                break;
            }
        }
    }

    @Override
    public void run() {
        sleep(1500);
        dump();

        // We continually add to the end of this list and read from the start. It's a buffer!
        ByteList buffer = new ByteList();

        while (true) {
            if (notifyStop) {
                notifyStop = false;
                port.closePort();
                break;
            }

            // Add new data to buffer
            buffer.addAll(read());

            // Process the buffer
            process(buffer);

            sleep(10);
        }
    }

    /**
     * Process data from the port.
     * @param buffer The data buffer.
     */
    private void process(ByteList buffer) {
        Transaction txn = updateTransaction(null).orElse(null);

        if (!buffer.isEmpty()) {
            System.out.println(buffer);
            System.out.println(txn);
        }

        if (!buffer.isEmpty()) {
            log.log(Level.DEBUG, "Buffer: " + buffer);
        }

        while (!buffer.isEmpty()) {
            byte first = buffer.get(0);

            // Check if the next byte is a status
            Status status = Status.fromByte(first).orElse(null);
            if (status != null) {
                log.log(Level.DEBUG, "Reading... " + new ByteList(first));
                log.log(Level.DEV, "Reading... " + status);
                if (txn != null) {
                    txn.handle(status);
                    txn = updateTransaction(txn).orElse(null);
                }
                buffer.remove(0);
            }
            else {
                // Expecting SOF at this point, not found?
                if (first != DataFrame.SOF) {
                    buffer.clear();
                    break;
                }

                // Length hasn't arrived yet.
                if (buffer.size() == 1) {
                    break;
                }

                buffer.remove(0);

                int length = (int) buffer.get(0);

                // Wait until we get the full frame.
                if (buffer.size() - 1 < length || length < 0) {
                    break;
                }

                ByteList data = buffer.subList(1, length + 1);
                DataFrameType type = DataFrameType.valueOf(data.remove(0));

                UnknownMessage unknown = new UnknownMessage(data, type);
                InboundMessage msg;

                // Todo: change this to buffer = sublist
                for (int i = 0; i < length + 1; ++i)
                    buffer.remove(0);

                // Existing transaction in progress...
                if (txn != null) {
                    msg = txn.handle(unknown);
                    txn = updateTransaction(txn).orElse(null);
                }
                else {
                    msg = InboundMessage.parse(unknown).orElse(null);
                }

                // Notify controller of message
                if (msg != null && !msg.equals(unknown)) {
                    controller.onReceive(msg);
                }

                log.log(Level.DEBUG, "Reading... " + data + " (" + type + ")");
                if (msg == null) {
                    log.log(Level.DEV, "Reading... " + unknown);
                }
                else {
                    log.log(Level.DEV, "Reading... " + msg);
                }

                write(Status.ACK);
            }
        }
    }

    /**
     * Helper method to sleep for a specified duration.
     * @param ms Duration in milliseconds.
     * @return True if the sleep was successful, false if otherwise.
     */
    private boolean sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Transceiver(thread=" + thread + ")";
    }
}
