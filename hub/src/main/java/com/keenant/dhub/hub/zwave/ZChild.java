package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.hub.event.FeatureChangeEvent;
import com.keenant.dhub.hub.event.NestedFeatureChangeEvent;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.zwave.feature.ChildrenZFeature;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.InboundEncap;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString(exclude = "node")
public class ZChild implements ZDevice {
    private final ZNode node;
    private final ChildrenZFeature children;
    private final int endPoint;
    private final List<CmdClass> cmdClasses;
    private List<ZFeature> features;

    public ZChild(ZNode node, ChildrenZFeature children, int endPoint, List<CmdClass> cmdClasses) {
        this.node = node;
        this.children = children;
        this.endPoint = endPoint;
        this.cmdClasses = cmdClasses;
    }

    public ZNode getNode() {
        return node;
    }

    public int getEndPoint() {
        return endPoint;
    }

    @Override
    public Network getNetwork() {
        return children;
    }

    @Override
    public <C extends Cmd<R>, R extends InboundCmd> Optional<R> send(C cmd, int timeout) {
        Optional<InboundEncap<R>> encap = node.send(CmdClass.MULTI_CHANNEL.encap(endPoint, cmd), timeout);
        return encap.map(InboundEncap::getCmd);
    }

    @Override
    public void subscribe(ZFeature feature) {
        node.subscribe(feature);
    }

    @Override
    public void unsubscribe(ZFeature feature) {
        node.unsubscribe(feature);
    }

    @Override
    public void start() {
        features = new ArrayList<>();
        for (CmdClass cmdClass : cmdClasses) {
            ZFeature.fromCmdClass(this, cmdClass).ifPresent(feature -> {
                features.add(feature);
            });
        }

        features.forEach(ZFeature::start);
    }

    @Override
    public void stop() {
        features.forEach(ZFeature::stop);
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(endPoint);
    }

    @Override
    public List<ZFeature> getFeatures() {
        return features;
    }

    @Override
    public void publish(DataFeature feature) {
        FeatureChangeEvent event = new FeatureChangeEvent(children, this, feature);
        children.publish(event);
    }
}
