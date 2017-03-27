package com.keenant.dhub.core.util;

/**
 * Something that can be represented as an array or list of bytes.
 */
public interface Byteable {
    /**
     * Converts this object to it's byte representation.
     * @return The byte representation.
     */
    ByteList toBytes();

    /**
     * Convert this object it's byte array representation.
     * @return The byte array representation.
     */
    default byte[] toByteArray() {
        return toBytes().toByteArray();
    }
}
