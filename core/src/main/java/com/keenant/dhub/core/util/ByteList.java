package com.keenant.dhub.core.util;

import java.util.ArrayList;
import java.util.Collection;

public class ByteList extends ArrayList<Byte> implements Byteable {
    public ByteList(Collection<Byte> bytes) {
        super(bytes);
    }

    public ByteList(byte... bytes) {
        for (byte bite : bytes)
            add(bite);
    }

    public ByteList(int... bytes) {
        for (int bite : bytes)
            add((byte) bite);
    }

    public ByteList(byte bite) {
        add(bite);
    }

    public ByteList(Byte bite) {
        add(bite);
    }

    @Override
    public ByteList subList(int fromIndex, int toIndex) {
        return new ByteList(super.subList(fromIndex, toIndex));
    }

    @Override
    public ByteList toBytes() {
        return this;
    }

    /**
     * Converts this list to an array of bytes.
     * @return The newly created array of bytes.
     */
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

    /**
     * Copy this list to a new list object.
     * @return The new list.
     */
    public ByteList cpy() {
        return new ByteList(this);
    }
}
