package com.keenant.dhub.zwave.frame;

public enum DataFrameType {
    REQUEST((byte) 0x00),
    RESPONSE((byte) 0x01);

    private final byte value;

    DataFrameType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    public static DataFrameType valueOf(byte value) {
        for (DataFrameType type : DataFrameType.values())
            if (type.value == value)
                return type;
        throw new IllegalArgumentException("Unknown data frame type for value: " + value + ".");
    }
}
