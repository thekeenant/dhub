package com.keenant.dhub.hub.plugin.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.network.Provider;
import com.keenant.dhub.hub.plugin.zwave.feature.ZBinaryFeature;
import com.keenant.dhub.hub.plugin.zwave.feature.ZMultilevelFeature;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import com.keenant.dhub.zwave.transaction.Transaction;
import lombok.ToString;

import java.util.logging.Logger;

@ToString(exclude = {"plugin", "port", "log"})
public class ZNetwork extends Network<ZDevice> {
    private final ZPlugin plugin;
    private final SerialPort port;
    private final Logger log;
    private final Controller controller;

    public ZNetwork(ZPlugin plugin, SerialPort port, Logger log) {
        this.plugin = plugin;
        this.port = port;
        this.log = log;
        controller = new Controller(port, log);
    }

    public SerialPort getPort() {
        return port;
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
            plugin.retry(port, this);
            stop();
            return;
        }

        long homeId = mem.getHomeId();
        int mainNode = mem.getNodeId();

        log.info("Received memory information: " + homeId + ", node " + mainNode);
        log.info("Fetching node list");

        NodeListMsg.Reply list = send(new NodeListMsg())
                .await(5000)
                .getReply()
                .orElse(null);

        if (list == null) {
            plugin.retry(port, this);
            stop();
            return;
        }

        log.info("Received node list: " + list.getNodeIds());
        log.info("Adding devices");

        for (int nodeId : list.getNodeIds()) {
            if (nodeId == mainNode) {
                continue;
            }

            addDevice(new ZDevice(this, nodeId));
        }

        log.info("Devices added, fetching updates");
        getDevices().forEach((device) -> {
            device.getFeatures().forEach(Feature::update);
            device.getProviders().forEach(Provider::update);
        });

        log.info("Network is ready");
    }

    @Override
    public void stop() {
        super.stop();
        controller.stop();
    }

    public <T extends Transaction> T send(Message<T> msg) {
        return controller.send(msg);
    }

    @Override
    public String getUniqueId() {
        return controller.getName();
    }
}
