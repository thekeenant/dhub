package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.SendDataTransaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZNode {
    private final ZNetwork network;
    private final int id;
    private final Map<Class<? extends InboundCmd>, InboundCmd> latestCmds;

    public ZNode(ZNetwork network, int id) {
        this.network = network;
        this.id = id;

        this.latestCmds = new HashMap<>();
    }

    public <C extends Cmd<R>, R extends InboundCmd> SendDataTransaction<C, R> send(C cmd) {
        return network.send(new SendDataMsg<>(id, cmd));
    }

    public <C extends Cmd<R>, R extends InboundCmd> SendDataTransaction<C, R> send(C cmd, Priority priority) {
        return network.send(new SendDataMsg<>(id, cmd), priority);
    }

    public void updateCmd(InboundCmd cmd) {
        latestCmds.put(cmd.getClass(), cmd);
    }

    @SuppressWarnings("unchecked")
    public <T extends InboundCmd> Optional<T> latestCmd(Class<T> cmd) {
        return Optional.ofNullable((T) latestCmds.get(cmd));
    }
}
