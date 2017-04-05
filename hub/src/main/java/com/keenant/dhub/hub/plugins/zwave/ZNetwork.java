package com.keenant.dhub.hub.plugins.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.util.EventListener;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.messages.AddNodeMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg;
import com.keenant.dhub.zwave.transaction.ReplyTransaction;
import lombok.ToString;

import java.util.*;

@ToString(exclude = "plugin", callSuper = true)
public class ZNetwork extends Controller implements EventListener, Network<ZNode> {
    private final ZPlugin plugin;
    private Map<Integer, ZNode> nodes;

    public ZNetwork(SerialPort port, ZPlugin plugin) throws IllegalArgumentException {
        super(port);

        this.plugin = plugin;
        nodes = new HashMap<>();
    }

    public Set<Integer> getNodeIds() {
        return nodes.keySet();
    }

    public Optional<ZNode> getNode(int id) {
        return Optional.ofNullable(nodes.get(id));
    }

    public Collection<ZNode> getNodes() {
        return nodes.values();
    }

    @Override
    public Collection<ZNode> getDevices() {
        return getNodes();
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public void loadDevices() {
        ReplyTransaction<NodeListMsg.Reply> txn = send(new NodeListMsg());
        txn.await(5000);

        NodeListMsg.Reply reply = txn.getReply().orElseThrow(RuntimeException::new);

        nodes = new HashMap<>();
        for (int nodeId : reply.getNodeIds()) {
            nodes.put(nodeId, new ZNode(this, nodeId));
        }

        send(new RemoveNodeMsg());
        send(new AddNodeMsg());
    }
}
