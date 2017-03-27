package com.keenant.dhub.zwave.frame;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;

public class UnknownDataFrame implements DataFrame, IncomingMessage {
    private final ByteList data;
    private final DataFrameType type;

    public UnknownDataFrame(ByteList data, DataFrameType type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public ByteList toDataBytes() {
        return data;
    }

    @Override
    public DataFrameType getType() {
        return type;
    }

    @Override
    public IncomingMessageEvent createEvent(Controller controller) {
        throw new UnsupportedOperationException("Unknown data frame has no message event.");
    }
}
