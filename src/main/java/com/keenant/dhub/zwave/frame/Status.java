package com.keenant.dhub.zwave.frame;

import com.keenant.dhub.util.ByteList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Status implements Frame {
    /**
     * Acknowledge
     */
    public final static Status ACK = new Status("ACK", (byte) 0x06);

    /**
     * Negative acknoweldge
     */
    public final static Status NAK = new Status("NAK", (byte) 0x15);

    /**
     * Can(cel) it.
     */
    public final static Status CAN = new Status("CAN", (byte) 0x18);

    /**
     * All the possible statuses.
     */
    public final static List<Status> ALL = Arrays.asList(ACK, NAK, CAN);

    private final String name;
    private final byte value;
    private final ByteList bytes;

    private Status(String name, byte value) {
        this.name = name;
        this.value = value;
        this.bytes = new ByteList(value);
    }

    public int getValue() {
        return this.value;
    }

    public ByteList toBytes() {
        return this.bytes;
    }

    @Override
    public String toString() {
        return String.format("Status(%s)", this.name);
    }

    public static Optional<Status> fromByte(byte bite) {
        for (Status status : ALL) {
            if (status.value == bite) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
