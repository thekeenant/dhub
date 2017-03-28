package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundCmd;
import lombok.ToString;

@ToString
public class CmdEvent<T extends InboundCmd> extends ControllerEvent {
    private final int nodeId;
    private final T cmd;

    public CmdEvent(Controller controller, int nodeId, T cmd) {
        super(controller);
        this.nodeId = nodeId;
        this.cmd = cmd;
    }

    public int getNodeId() {
        return nodeId;
    }

    public T getCmd() {
        return cmd;
    }
}
