package com.keenant.dhub.util;

/**
 * Things that can be converted to a ByteList.
 */
public interface Byteable {
    /**
     * Gets the bytes that represent this object.
     * @return The bytes.
     */
    ByteList toBytes();

    /**
     * Convenience method to convert this object to a byte array.
     * @return
     */
    default byte[] toByteArray() {
        return toBytes().toByteArray();
    }
}
