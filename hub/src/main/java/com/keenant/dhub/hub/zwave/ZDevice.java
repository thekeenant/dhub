package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.Lifecycle;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundCmd;

import java.util.Optional;

public interface ZDevice extends Device, Lifecycle {
    Controller getController();

    default <C extends Cmd<R>, R extends InboundCmd> Optional<R> send(C cmd) {
        return send(cmd, 5000);
    }

    <C extends Cmd<R>, R extends InboundCmd> Optional<R> send(C cmd, int timeout);
}
