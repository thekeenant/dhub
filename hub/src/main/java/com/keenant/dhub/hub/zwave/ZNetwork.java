package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.event.message.AddNodeCallbackEvent;
import com.keenant.dhub.zwave.event.message.ApplicationCommandEvent;
import com.keenant.dhub.zwave.event.message.NodeListReplyEvent;
import com.keenant.dhub.zwave.event.message.RemoveNodeCallbackEvent;
import com.keenant.dhub.zwave.messages.AddNodeMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg.State;
import lombok.ToString;
import net.engio.mbassy.listener.Handler;

import java.util.*;

@ToString
public class ZNetwork extends Controller implements Listener {
    private final ZPlugin plugin;
    private final Map<Integer, ZNode> nodes;

    private final List<InboundCmd> cmds;

    public ZNetwork(SerialPort port, ZPlugin plugin) throws IllegalArgumentException {
        super(port);

        this.plugin = plugin;
        nodes = new HashMap<>();
        cmds = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        super.start();
        subscribe(this);

        send(new NodeListMsg());
    }

    public void stop() {
        super.stop();
        unsubscribe(this);
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

    @Handler
    public void onCmd(ApplicationCommandEvent event) {
        getNode(event.getMessage().getNodeId()).ifPresent((node) -> {
            node.updateCmd(event.getMessage().getCmd());
        });
    }

    @Handler
    public void onAddNode(AddNodeCallbackEvent event) {
        if (event.getMessage().getState() == AddNodeMsg.State.DONE) {
            send(new NodeListMsg(), Priority.HIGHEST);
        }
    }

    @Handler
    public void onRemoveNode(RemoveNodeCallbackEvent event) {
        if (event.getMessage().getState() == State.DONE) {
            send(new NodeListMsg(), Priority.HIGHEST);
        }
    }

    @Handler
    public void onNodeListReply(NodeListReplyEvent event) {
        nodes.clear();

        for (int nodeId : event.getMessage().getNodeIds()) {
            nodes.put(nodeId, new ZNode(this, nodeId));
        }
    }
}
