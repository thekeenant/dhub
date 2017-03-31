package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.hub.Plugin;
import com.keenant.dhub.hub.web.WebPlugin;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.BasicCmd;
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
import java.util.stream.Collectors;

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
        Logging.setLevel(Level.DEV);

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
        Gson gson = new Gson();

        getHub().getPlugin(WebPlugin.class).ifPresent((plugin) -> {
            plugin.api("/zwave", (http) -> {

                http.path("/networks", () -> {
                    http.get("", (req, res) -> {
                        return networks.stream().map(ZNetwork::getName).collect(Collectors.toList());
                    });

                });

                http.get("/networks/:network/nodes", (req, res) -> {
                    Optional<ZNetwork> network = getNetwork(req.params("network"));
                    return network.map(ZNetwork::getNodeIds);
                });

                http.get("/networks/:network/nodes/:id/basic/set/:level", (req, res) -> {
                    String networkName = req.params("network");
                    int id = Integer.parseInt(req.params("id"));
                    int level = Integer.parseInt(req.params("level"));

                    getNetwork(networkName).ifPresent((network) -> {
                        network.getNode(id).ifPresent((node) -> {
                            node.send(CmdClass.BASIC.set(level));
                        });
                    });

                    return "sent";
                });

                http.get("/networks/:network/nodes/:id/basic/get", (req, res) -> {
                    String networkName = req.params("network");
                    int id = Integer.parseInt(req.params("id"));

                    ZNetwork network = getNetwork(networkName).orElse(null);

                    if (network != null) {
                        ZNode node = network.getNode(id).orElse(null);

                        if (node != null) {
                            node.send(CmdClass.BASIC.get());
                            return node.latestCmd(BasicCmd.Report.class);
                        }

                    }

                    return "sent";
                });
            });
        });

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
