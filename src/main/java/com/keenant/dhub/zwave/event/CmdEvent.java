package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.cmd.Cmd;
import lombok.ToString;

@ToString
public class CmdEvent {
    private final ZController controller;
    private final int nodeId;
    private final Cmd cmd;

    public CmdEvent(ZController controller, int nodeId, Cmd cmd) {
        this.controller = controller;
        this.nodeId = nodeId;
        this.cmd = cmd;
    }

    public ZController getController() {
        return controller;
    }

    public int getNodeId() {
        return nodeId;
    }

    public Cmd getCmd() {
        return cmd;
    }
}
