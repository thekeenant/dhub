package com.keenant.dhub.hub.plugins.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.util.ControllerListener;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import lombok.ToString;

import java.util.*;

@ToString(exclude = "plugin", callSuper = true)
public class ZNetwork extends Controller implements ControllerListener, Network {
    private final ZPlugin plugin;
    private ZNode mainNode;
    private Map<Integer, ZNode> devices;

    public ZNetwork(SerialPort port, ZPlugin plugin) throws IllegalArgumentException {
        super(port);

        this.plugin = plugin;
        devices = new HashMap<>();
    }

    public Set<Integer> getNodeIds() {
        return devices.keySet();
    }

    public Optional<ZNode> getNode(int nodeId) {
        return Optional.ofNullable(devices.get(nodeId));
    }

    @Override
    public Collection<ZNode> getDevices() {
        return devices.values();
    }

    @Override
    public String getUniqueId() {
        return getName();
    }

    @Override
    public void start() {
        try {
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
            mainNode = new ZNode(this, memory.getNodeId());

            for (int nodeId : nodeList.getNodeIds()) {
                if (nodeId == mainNode.getId()) {
                    continue;
                }

                ZNode node = new ZNode(this, nodeId);
                node.start();
                devices.put(nodeId, node);
            }

        } catch (Exception e) {

        }


    }

    @Override
    public void stop() {
        super.stop();
    }
}
