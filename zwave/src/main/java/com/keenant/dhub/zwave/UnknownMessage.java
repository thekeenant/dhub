package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import lombok.ToString;

/**
 * A data frame that is not (yet) known.
 */
@ToString
public class UnknownMessage implements InboundMessage {
    private final ByteList data;
    private final DataFrameType type;

    public UnknownMessage(ByteList data, DataFrameType type) {
        this.data = data;
        this.type = type;
    }

    public ByteList getDataBytes() {
        return data;
    }

    @Override
    public DataFrameType getType() {
        return type;
    }

    @Override
    public InboundMessageEvent createEvent(Controller controller) {
        throw new UnsupportedOperationException("Unknown data frame has no message event.");
    }
}
