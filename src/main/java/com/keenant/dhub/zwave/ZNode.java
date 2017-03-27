package com.keenant.dhub.zwave;

import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.cmd.Cmd;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;

public class ZNode {
    private final ZController controller;
    private final byte id;

    public ZNode(ZController controller, byte id) {
        this.controller = controller;
        this.id = id;
    }

    public ReqResTransaction<Response> sendCmd(Cmd cmd) {
        return sendCmd(cmd, Priority.DEFAULT);
    }

    public ReqResTransaction<Response> sendCmd(Cmd cmd, Priority priority) {
        SendDataMsg msg = new SendDataMsg(id, cmd, cmd.isResponseExpected());
        return sendData(msg, priority);
    }

    public ReqResTransaction<Response> sendData(SendDataMsg message) {
        return sendData(message, Priority.DEFAULT);
    }

    public ReqResTransaction<Response> sendData(SendDataMsg message, Priority priority) {
        ReqResTransaction<Response> txn = message.createTransaction(priority);
        controller.queue(txn);
        return txn;
    }
}
