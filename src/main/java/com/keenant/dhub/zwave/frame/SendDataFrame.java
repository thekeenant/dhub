package com.keenant.dhub.zwave.frame;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Byteable;

public class SendDataFrame implements DataFrame {
    private final byte nodeId;
    private final Byteable data;

    public SendDataFrame(byte nodeId, Byteable data) {
        this.nodeId = nodeId;
        this.data = data;
    }

    public SendDataFrame(int nodeId, Byteable data) {
        this((byte) nodeId, data);
    }

    @Override
    public ByteList toDataBytes() {
        ByteList data = this.data.toBytes();

        ByteList bites = new ByteList();
        bites.add((byte) 0x13);
        bites.add(nodeId);
        bites.add((byte) data.size());
        bites.addAll(data);
        bites.add((byte) 0);
        bites.add((byte) 0);
        return bites;
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQUEST;
    }
}
