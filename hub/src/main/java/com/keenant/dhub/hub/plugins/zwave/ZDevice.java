package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.core.util.EventListener;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.SendDataTransaction;
import lombok.ToString;

@ToString(exclude = "network")
public class ZDevice implements Device, EventListener {
    private final ZNetwork network;
    private final int id;

    public ZDevice(ZNetwork network, int id) {
        this.network = network;
        this.id = id;
    }

    public <C extends Cmd<R>, R extends InboundCmd> SendDataTransaction<C, R> send(C cmd) {
        return network.send(new SendDataMsg<>(id, cmd));
    }

    public int getNodeId() {
        return id;
    }

    public void start() {

    }

    public void stop() {

    }

    @Override
    public String getUniqueId() {
        return String.valueOf(getNodeId());
    }
}
