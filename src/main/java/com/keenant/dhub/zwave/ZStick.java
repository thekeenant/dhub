package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.zwave.frame.DataFrame;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.transaction.Transaction;
import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Byteable;

import java.util.Optional;

public class ZStick implements Runnable {
    private final ZController controller;
    private final SerialPort port;

    private boolean stopped;

    public ZStick(ZController controller, SerialPort port) {
        this.controller = controller;
        this.port = port;
    }

    public void start() {
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

    public void writeRaw(byte[] bites) {
        port.writeBytes(bites, bites.length);
    }

    public void write(Byteable bites) {
        writeRaw(bites.toByteArray());
        System.out.println("-> " + bites.toBytes());
    }

    @Override
    public void run() {
        port.openPort();

        while (true) {
            read();
            if (port.bytesAvailable() > 0) {
                sleep(100);
            }
            else {
                break;
            }
        }

        ByteList buffer = read();

        boolean dataframe = false;

        while (true) {
            if (stopped) {
                port.closePort();
                break;
            }

            // Add new data to buffer
            buffer.addAll(read());

            while (!buffer.isEmpty()) {
                byte first = buffer.get(0);

                if (!dataframe) {
                    Optional<Status> status = Status.fromByte(first);
                    status.ifPresent(status1 -> {
                        System.out.println("<- " + new ByteList(first));
                        buffer.remove(0);
                    });
                }

                // Expecting SOF at this point, not found?
                if (first != DataFrame.SOF) {
                    buffer.clear();
                    break;
                }
                buffer.remove(0);

                dataframe = true;

                int length = buffer.get(0);

                // Wait until we get the full frame.
                if (buffer.size() - 1 < length) {
                    break;
                }

                ByteList frame = buffer.subList(1, length + 1);
                System.out.println("<- " + frame);

                write(Status.ACK);

                for (int i = 0; i < length + 1; ++i)
                    buffer.remove(0);

                dataframe = false;
            }

            sleep(50);
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
