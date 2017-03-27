package com.keenant.dhub.zwave.frame;

/**
 * There are two types of data frames in Z-Wave, requests and responses.
 */
public class DataFrameType {
    public static final DataFrameType REQ = new DataFrameType(0x00);
    public static final DataFrameType RES = new DataFrameType(0x01);

    private final byte value;

    private DataFrameType(int value) {
        this.value = (byte) value;
    }

    @Override
    public String toString() {
        return this == REQ ? "REQ" : "RES";
    }

    public byte getValue() {
        return this.value;
    }

    public static DataFrameType valueOf(byte value) {
        switch (value) {
            case 0x00:
                return REQ;
            case 0x01:
                return RES;
            default:
                throw new IllegalArgumentException("Unknown data frame type for value: " + value + ".");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Byte) {
            return value == (byte) obj;
        }
        else if (obj instanceof DataFrameType) {
            return value == ((DataFrameType) obj).value;
        }

        return false;
    }
}
