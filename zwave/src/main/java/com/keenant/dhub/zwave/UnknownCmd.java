package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.event.CmdEvent;
import lombok.ToString;

@ToString
public class UnknownCmd implements InboundCmd {
    private final ByteList data;

    public UnknownCmd(ByteList data) {
        this.data = data;
    }

    public ByteList getData() {
        return data;
    }

    @Override
    public CmdEvent<?> createEvent(Controller controller, int nodeId) {
        return new CmdEvent<>(controller, nodeId, this);
    }
}
