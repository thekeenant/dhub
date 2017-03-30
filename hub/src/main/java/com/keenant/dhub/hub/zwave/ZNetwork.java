package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.message.AddNodeCallbackEvent;
import com.keenant.dhub.zwave.event.message.NodeListReplyEvent;
import com.keenant.dhub.zwave.event.message.RemoveNodeCallbackEvent;
import com.keenant.dhub.zwave.messages.AddNodeMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg.State;
import net.engio.mbassy.listener.Handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ZNetwork extends Controller implements Listener {
    private final ZPlugin server;
    private final Map<Integer, ZNode> nodes;

    public ZNetwork(SerialPort port, ZPlugin server) throws IllegalArgumentException {
        super(port);
        this.server = server;
        this.nodes = new HashMap<>();
    }

    public void start() {
        super.start();
        subscribe(this);

        send(NodeListMsg.get());
    }

    public void stop() {
        super.stop();
        unsubscribe(this);
    }

    public Set<Integer> getNodeIds() {
        return nodes.keySet();
    }

    public Collection<ZNode> getNodes() {
        return nodes.values();
    }

    @Handler
    public void onAddNode(AddNodeCallbackEvent event) {
        if (event.getMessage().getState() == AddNodeMsg.State.DONE) {
            send(NodeListMsg.get(), Priority.HIGHEST);
        }
    }

    @Handler
    public void onRemoveNode(RemoveNodeCallbackEvent event) {
        if (event.getMessage().getState() == State.DONE) {
            send(NodeListMsg.get(), Priority.HIGHEST);
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
