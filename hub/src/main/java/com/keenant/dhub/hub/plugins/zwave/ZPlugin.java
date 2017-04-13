package com.keenant.dhub.hub.plugins.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.plugin.Plugin;
import io.airlift.airline.Cli.CliBuilder;

public class ZPlugin extends Plugin {
    @Override
    public void load(CliBuilder<Runnable> cli) {

    }

    @Override
    public void enable() {
        for (SerialPort port : SerialPort.getCommPorts()) {
            Hub.getHub().getNetworkManager().register(new ZNetwork(port, getLogger()));
        }
    }

    @Override
    public void disable() {

    }
}
