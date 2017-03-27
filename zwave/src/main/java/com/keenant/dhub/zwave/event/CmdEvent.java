package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingCmd;
import lombok.ToString;

@ToString
public class CmdEvent<T extends IncomingCmd> {
    private final Controller controller;
    private final int nodeId;
    private final T cmd;

    public CmdEvent(Controller controller, int nodeId, T cmd) {
        this.controller = controller;
        this.nodeId = nodeId;
        this.cmd = cmd;
    }

    public Controller getController() {
        return controller;
    }

    public int getNodeId() {
        return nodeId;
    }

    public T getCmd() {
        return cmd;
    }
}
