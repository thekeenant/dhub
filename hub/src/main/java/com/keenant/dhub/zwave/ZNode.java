package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;

public class ZNode {
    private final Controller controller;
    private final int id;

    public ZNode(Controller controller, int id) {
        this.controller = controller;
        this.id = id;
    }

    public ReqResTransaction<Response> queueCmd(OutgoingCmd cmd) {
        return queueCmd(cmd, Priority.DEFAULT);
    }

    public ReqResTransaction<Response> queueCmd(OutgoingCmd cmd, Priority priority) {
        SendDataMsg msg = new SendDataMsg((byte) id, cmd, cmd.isResponseExpected());
        return queueData(msg, priority);
    }

    public ReqResTransaction<Response> queueData(SendDataMsg message) {
        return queueData(message, Priority.DEFAULT);
    }

    public ReqResTransaction<Response> queueData(SendDataMsg message, Priority priority) {
        return controller.queue(message, priority);
    }
}
