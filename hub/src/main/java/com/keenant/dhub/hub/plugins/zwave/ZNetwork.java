package com.keenant.dhub.hub.plugins.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.logging.Logger;

public class ZNetwork extends Network<ZDevice> {
    private final Controller controller;

    public ZNetwork(SerialPort port, Logger log) {
        this.controller = new Controller(port, log);
    }

    @Override
    public void start() {
        super.start();

        controller.start();

        MemoryGetIdMsg.Reply mem = send(new MemoryGetIdMsg())
                .await(5000)
                .getReply()
                .orElse(null);

        if (mem == null) {
            return;
        }

        long homeId = mem.getHomeId();
        int mainNode = mem.getNodeId();

        NodeListMsg.Reply list = send(new NodeListMsg())
                .await(5000)
                .getReply()
                .orElse(null);

        for (int nodeId : list.getNodeIds()) {
            if (nodeId == mainNode) {
                continue;
            }

            addDevice(new ZDevice(this, nodeId));
        }
    }

    public <T extends Transaction> T send(Message<T> msg) {
        return controller.send(msg);
    }

    @Override
    public String getUniqueId() {
        return controller.getName();
    }
}
