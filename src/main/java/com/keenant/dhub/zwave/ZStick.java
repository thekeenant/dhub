package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.logging.Level;
import com.keenant.dhub.logging.Logging;
import com.keenant.dhub.zwave.frame.*;
import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Byteable;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.logging.Logger;

public class ZStick implements Runnable {
    private final ZController controller;
    private final SerialPort port;
    private final Logger log;

    private boolean stopped;

    public ZStick(ZController controller, SerialPort port) {
        this.controller = controller;
        this.port = port;
        this.log = Logging.getLogger(port.getSystemPortName());
    }

    public void start() {
        port.openPort();

        sleep(1500);
        while (port.bytesAvailable() > 0) {
            read();
            sleep(10);
        }

        new Thread(this).start();
    }

    public void stop() {
        stopped = true;
    }

    private byte[] readRaw() {
        if (port.bytesAvailable() <= 0) {
            return new byte[0];
        }
        byte[] bites = new byte[port.bytesAvailable()];
        port.readBytes(bites, bites.length);
        return bites;
    }

    private ByteList read() {
        return new ByteList(readRaw());
    }

    public void write(Byteable byteable) {
        log.log(Level.DEBUG, "Writing... " + byteable.toBytes());
        log.log(Level.DEV, "Writing... " + byteable);

        byte[] arr = byteable.toByteArray();
        port.writeBytes(arr, arr.length);
    }

    private Transaction updateTransaction(Transaction txn) {
        if (txn == null) {
            txn = controller.getCurrent().orElse(null);
        }

        while (txn != null && !txn.getOutgoing().isEmpty()) {
            write(txn.getOutgoing().poll());
            sleep(10);
        }

        return txn;
    }

    @Override
    public void run() {
        ByteList buffer = new ByteList();

        boolean dataframe = false;

        while (true) {
            if (stopped) {
                port.closePort();
                break;
            }

            // Add new data to buffer
            buffer.addAll(read());

            Transaction txn = updateTransaction(null);

            while (!buffer.isEmpty()) {
                byte first = buffer.get(0);

                if (!dataframe) {
                    Status status = Status.fromByte(first).orElse(null);
                    if (status != null) {
                        log.log(Level.DEBUG, "Reading... " + new ByteList(first));
                        log.log(Level.DEV, "Reading... " + status);
                        if (txn != null) {
                            txn.handle(status);
                            txn = updateTransaction(txn);
                        }
                        buffer.remove(0);
                    }
                }

                // Expecting SOF at this point, not found?
                if (first != DataFrame.SOF) {
                    buffer.clear();
                    break;
                }

                dataframe = true;

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

                // Existing transaction in progress...
                if (txn != null) {
                    ByteList data = buffer.subList(1, length + 1);
                    DataFrameType type = DataFrameType.valueOf(data.remove(0));

                    IncomingDataFrame frame = new IncomingDataFrame(data, type);
                    IncomingDataFrame resolved = txn.handle(frame);

                    log.log(Level.DEBUG, "Reading... " + data);
                    log.log(Level.DEV, "Reading... " + resolved);

                    txn = updateTransaction(txn);
                }
                // Something unexpected came?
                else {
                    ByteList data = buffer.subList(1, length + 1);
                    DataFrameType type = DataFrameType.valueOf(data.remove(0));

                    // Todo: Deal with this later...
                    log.log(Level.DEBUG, "Reading... " + data + " (Ignored)");
                    log.log(Level.DEBUG, "Reading... Unknown");

                    write(Status.ACK);
                }

                for (int i = 0; i < length + 1; ++i)
                    buffer.remove(0);

                dataframe = false;
            }

            sleep(10);
        }
    }

    private boolean sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
