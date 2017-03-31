package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.ReplyCallbackTransaction;

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

    public void send(BasicCmd.Set cmd) {
        network.send(new SendDataMsg(id, cmd));
        send(CmdClass.BASIC.get());
    }

    public void send(BasicCmd.Get cmd) {
        network.send(new SendDataMsg(id, cmd));
    }

    public void send(Cmd cmd, Priority priority) {
        network.send(new SendDataMsg(id, cmd), priority);
    }

    public void updateCmd(InboundCmd cmd) {
        latestCmds.put(cmd.getClass(), cmd);
    }

    public <T extends InboundCmd> Optional<T> latestCmd(Class<T> cmd) {
        return Optional.ofNullable((T) latestCmds.get(cmd));
    }
}
