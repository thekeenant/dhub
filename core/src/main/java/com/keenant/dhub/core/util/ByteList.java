package com.keenant.dhub.core.util;

import java.util.ArrayList;
import java.util.Collection;

public class ByteList extends ArrayList<Byte> implements Byteable {
    /**
     * Copy an existing collection of bytes.
     * @param bytes Existing byte collection.
     */
    public ByteList(Collection<Byte> bytes) {
        super(bytes);
    }

    /**
     * Constructor.
     * @param bytes Initial population of bytes.
     */
    public ByteList(byte... bytes) {
        for (byte bite : bytes)
            add(bite);
    }

    /**
     * Constructor.
     * @param bytes Initial population of bytes.
     */
    public ByteList(int... bytes) {
        for (int bite : bytes)
            add((byte) bite);
    }

    /**
     * Copy this list to a new list object.
     * @return The new list.
     */
    public ByteList cpy() {
        return new ByteList(this);
    }

    @Override
    public ByteList subList(int fromIndex, int toIndex) {
        return new ByteList(super.subList(fromIndex, toIndex));
    }

    @Override
    public ByteList toBytes() {
        return this;
    }

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[size()];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = get(i);
        return bytes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ByteList{");
        for (int i = 0; i < size() - 1; i++)
            builder.append(String.format("0x%02X ", get(i)));
        if (size() > 0)
            builder.append(String.format("0x%02X", get(size() - 1)));
        builder.append("}");
        return builder.toString();
    }
}
