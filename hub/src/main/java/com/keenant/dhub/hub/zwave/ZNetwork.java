package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.zwave.ControllerListener;
import com.keenant.dhub.hub.event.NetworkEvent;
import com.keenant.dhub.hub.network.DeviceCollection;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import lombok.ToString;
import net.engio.mbassy.bus.MBassador;

@ToString(exclude = "plugin", callSuper = true)
public class ZNetwork extends Controller implements ControllerListener, Network {
    private final ZPlugin plugin;
    private final DeviceCollection<ZNode> devices = new DeviceCollection<>();
    private final MBassador<NetworkEvent> bus = new MBassador<>();
    private ZNode mainNode;

    public ZNetwork(SerialPort port, ZPlugin plugin) throws IllegalArgumentException {
        super(port);
        this.plugin = plugin;
    }

    @Override
    public DeviceCollection<ZNode> getDevices() {
        return devices;
    }

    @Override
    public void subscribe(NetworkListener listener) {
        bus.subscribe(listener);
    }

    @Override
    public void unsubscribe(NetworkListener listener) {
        bus.unsubscribe(listener);
    }

    @Override
    public void publish(NetworkEvent event) {
        bus.publishAsync(event);
    }

    @Override
    public String getUniqueId() {
        return getName();
    }

    @Override
    public void start() {
        try {
            devices.clear();

            super.start();

            NodeListMsg.Reply nodeList = send(new NodeListMsg())
                    .await(5000)
                    .getReply()
                    .orElseThrow(RuntimeException::new);

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
                devices.add(node);
            }

        } catch (Exception e) {

        }


    }

    @Override
    public void stop() {
        super.stop();
    }
}
