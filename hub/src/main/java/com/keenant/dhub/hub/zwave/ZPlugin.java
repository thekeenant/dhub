package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.hub.plugin.Plugin;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.event.message.MemoryGetIdReplyEvent;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Cli.GroupBuilder;
import lombok.ToString;
import net.engio.mbassy.listener.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ToString
public class ZPlugin extends Plugin {
    private static final Logger log = Logging.getLogger("ZPlugin");

    private List<ZNetwork> networks;

    public Optional<ZNetwork> getByName(String name) throws IllegalArgumentException {
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
        GroupBuilder<Runnable> cmd = cli.withGroup("zserver");
        cmd.withDefaultCommand(ZCommand.class);

        List<ZNetwork> networks = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            networks.add(new ZNetwork(port, this));
        }

        this.networks = new ArrayList<>();
        this.networks.addAll(networks);
    }

    @Override
    public void start() {
        networks.forEach(ZNetwork::start);
    }

    @Override
    public void stop() {
        networks.forEach(ZNetwork::stop);
    }

    public List<ZNetwork> getNetworks() {
        return networks;
    }

    private final class ZServerListener implements Listener {
        @Handler
        public void onTransactionComplete(TransactionCompleteEvent event) {
            log.info(event + " Complete");
        }

        @Handler
        public void onMemoryGetIdEvent(MemoryGetIdReplyEvent event) {
            long homeId = event.getMessage().getHomeId();
            int nodeId = event.getMessage().getNodeId();

            log.info("---" + event.getController() + "---");
            log.info("Home ID: " + homeId);
            log.info("Node ID: " + nodeId);
        }
    }
}
