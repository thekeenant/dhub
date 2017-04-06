package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.plugins.zwave.feature.BinaryGetZFeature;
import com.keenant.dhub.hub.plugins.zwave.feature.BinarySetZFeature;
import com.keenant.dhub.hub.plugins.zwave.feature.LevelGetZFeature;
import com.keenant.dhub.hub.plugins.zwave.feature.LevelSetZFeature;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;
import com.keenant.dhub.zwave.messages.ApplicationUpdateMsg;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg.Reply;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.ReplyCallbackTransaction;
import com.keenant.dhub.zwave.transaction.SendDataTransaction;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(exclude = "network")
public class ZNode implements Device<ZFeature> {
    private final ZNetwork network;
    private final int id;
    private List<ZFeature> features;

    public ZNode(ZNetwork network, int id) {
        this.network = network;
        this.id = id;
    }

    public <C extends Cmd<R>, R extends InboundCmd> SendDataTransaction<C, R> send(C cmd) {
        return network.send(new SendDataMsg<>(id, cmd));
    }

    public <C extends Cmd<R>, R extends InboundCmd> SendDataTransaction<C, R> send(C cmd, Priority priority) {
        return network.send(new SendDataMsg<>(id, cmd), priority);
    }

    @Override
    public String getId() {
        return id + "";
    }

    public int getNodeId() {
        return id;
    }

    @Override
    public void load() {
        ReplyCallbackTransaction<Reply, ApplicationUpdateMsg> txn = network.send(new RequestNodeInfoMsg(id));
        txn.await(10000);

        txn.getCallback().ifPresent((msg) -> {
            features = new ArrayList<>();

            for (CmdClass cmd : msg.getCmdClasses()) {
                if (cmd instanceof SwitchMultilevelCmd) {
                    features.add(new LevelSetZFeature(this));
                    features.add(new LevelGetZFeature(this));
                }
                else if (cmd instanceof SwitchBinaryCmd) {
                    features.add(new BinarySetZFeature(this));
                    features.add(new BinaryGetZFeature(this));
                }
            }
        });
    }

    @Override
    public void reload() {
        features = new ArrayList<>();
        load();
    }

    @Override
    public boolean isConnected() {
        // Todo?
        return true;
    }

    @Override
    public boolean isReady() {
        return features != null;
    }

    @Override
    public List<ZFeature> getFeatures() {
        return features;
    }
}
