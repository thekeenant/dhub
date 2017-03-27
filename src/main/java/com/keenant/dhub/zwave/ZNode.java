package com.keenant.dhub.zwave;

import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.cmd.OutgoingCmd;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;

public class ZNode {
    private final ZController controller;
    private final int id;

    public ZNode(ZController controller, int id) {
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
