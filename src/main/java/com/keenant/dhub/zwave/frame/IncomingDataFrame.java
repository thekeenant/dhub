package com.keenant.dhub.zwave.frame;

import com.keenant.dhub.util.ByteList;
import lombok.ToString;

@ToString
public class IncomingDataFrame implements DataFrame {
    private final ByteList data;
    private final DataFrameType type;

    public IncomingDataFrame(ByteList data, DataFrameType type) {
        this.data = data;
        this.type = type;
    }

    public IncomingDataFrame(ByteList data) {
        this(data, DataFrameType.RES);
    }

    @Override
    public ByteList toDataBytes() {
        return data;
    }

    @Override
    public DataFrameType getType() {
        return type;
    }
}
