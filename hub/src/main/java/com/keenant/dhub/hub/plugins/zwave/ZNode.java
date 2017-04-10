package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.core.util.EventListener;
import com.keenant.dhub.hub.plugins.zwave.feature.BinaryZFeature;
import com.keenant.dhub.hub.plugins.zwave.feature.ChildrenZFeature;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.messages.ApplicationUpdateMsg;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.SendDataTransaction;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString(exclude = "network")
public class ZNode implements ZDevice, EventListener {
    private final ZNetwork network;
    private final int id;
    private final List<ZFeature> features;

    public ZNode(ZNetwork network, int id) {
        this.network = network;
        this.id = id;
        this.features = new ArrayList<>();
    }

    @Override
    public <C extends Cmd<R>, R extends InboundCmd> Optional<R> send(C cmd, int timeout) {
        SendDataTransaction<C, R> txn = network.send(new SendDataMsg<>(id, cmd));
        txn.await(timeout);
        return txn.getResponse();
    }

    @Override
    public void subscribe(ZFeature feature) {
        network.subscribe(feature);
    }

    @Override
    public void unsubscribe(ZFeature feature) {
        network.unsubscribe(feature);
    }

    public int getId() {
        return id;
    }

    public void start() {
        features.clear();

        ApplicationUpdateMsg nodeInfo = network.send(new RequestNodeInfoMsg(id)).await(5000).getCallback().orElse(null);

        for (CmdClass cmd : nodeInfo.getCmdClasses()) {
            ZFeature feature = ZFeature.fromCmdClass(this, cmd).orElse(null);

            if (feature != null) {
                features.add(feature);
                network.subscribe(feature);
            }
        }

        features.forEach(ZFeature::start);
    }

    public void stop() {
        features.forEach(ZFeature::stop);
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(getId());
    }

    @Override
    public List<ZFeature> getFeatures() {
        return features;
    }
}
