package com.keenant.dhub.hub.plugins.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.util.EventListener;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import lombok.ToString;

import java.util.*;

@ToString(exclude = "plugin", callSuper = true)
public class ZNetwork extends Controller implements EventListener, Network<ZDevice> {
    private final ZPlugin plugin;
    private ZDevice mainNode;
    private Map<Integer, ZDevice> devices;

    public ZNetwork(SerialPort port, ZPlugin plugin) throws IllegalArgumentException {
        super(port);

        this.plugin = plugin;
        devices = new HashMap<>();
    }

    public Set<Integer> getNodeIds() {
        return devices.keySet();
    }

    public Optional<ZDevice> getDevice(int nodeId) {
        return Optional.ofNullable(devices.get(nodeId));
    }

    @Override
    public Collection<ZDevice> getDevices() {
        return getDevices();
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public void start() {
        super.start();

        NodeListMsg.Reply nodeList = send(new NodeListMsg())
                .await(5000)
                .getReply()
                .orElseThrow(RuntimeException::new);
        devices = new HashMap<>();

        MemoryGetIdMsg.Reply memory = send(new MemoryGetIdMsg())
                .await(5000)
                .getReply()
                .orElseThrow(RuntimeException::new);
        mainNode = new ZDevice(this, memory.getNodeId());

        for (int nodeId : nodeList.getNodeIds()) {
            if (nodeId == mainNode.getNodeId()) {
                continue;
            }

            ZDevice device = new ZDevice(this, nodeId);
            devices.put(nodeId, device);
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}
