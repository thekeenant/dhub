package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.hub.Plugin;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Cli.GroupBuilder;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ToString
public class ZPlugin extends Plugin {
    private static final Logger log = Logging.getLogger("ZPlugin");

    private List<ZNetwork> networks;

    public Optional<ZNetwork> getNetwork(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        return networks.stream().filter(c -> c.getName().equals(name)).findAny();
    }

    /**
     * Initializes this server with all the serial ports available.
     */
    @Override
    public void init(CliBuilder<Runnable> cli) {
        // Todo
        GroupBuilder<Runnable> cmd = cli.withGroup("zserver");

        List<ZNetwork> networks = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            networks.add(new ZNetwork(port, this));
        }

        this.networks = new ArrayList<>();
        this.networks.addAll(networks);

        // Register networks
        this.networks.forEach(getHub()::registerNetwork);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        networks.forEach(ZNetwork::stop);
    }
}
