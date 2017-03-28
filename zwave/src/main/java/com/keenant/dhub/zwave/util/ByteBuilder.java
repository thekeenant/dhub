package com.keenant.dhub.zwave.util;

public abstract class ByteBuilder {
    private byte current;

    public ByteBuilder() {
        current = getDefault();
    }

    protected ByteBuilder with(byte with) {
        current = (byte) (current | with);
        return this;
    }

    protected void reset() {
        current = getDefault();
    }

    protected byte getDefault() {
        return (byte) 0x00;
    }

    public byte get() {
        return current;
    }

    public String toString() {
        return String.format("0x%02X", current);
    }
}
