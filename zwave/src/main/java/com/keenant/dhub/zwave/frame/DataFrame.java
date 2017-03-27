package com.keenant.dhub.zwave.frame;

import com.keenant.dhub.core.util.ByteList;

public interface DataFrame extends Frame {
    byte SOF = 0x01;

    ByteList toDataBytes();

    DataFrameType getType();

    @Override
    default ByteList toBytes() {
        ByteList data = toDataBytes();

        ByteList bytes = new ByteList(
                (byte) 0x01,
                (byte) (data.size() + 2),
                getType().getValue()
        );
        bytes.addAll(data);

        // Checksum
        byte checksum = (byte) 0xFF;
        for (int i = 1; i < bytes.size(); i++)
            checksum ^= bytes.get(i);
        bytes.add(checksum);

        return bytes;
    }
}
