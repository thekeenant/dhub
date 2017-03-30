package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.messages.SendDataMsg;

public class ZNode {
    private final ZNetwork network;
    private final int id;

    public ZNode(ZNetwork network, int id) {
        this.network = network;
        this.id = id;
    }

    public void send(Cmd cmd) {
        network.send(SendDataMsg.of(id, cmd));
    }

    public void send(Cmd cmd, Priority priority) {
        network.send(SendDataMsg.of(id, cmd), priority);
    }
}
