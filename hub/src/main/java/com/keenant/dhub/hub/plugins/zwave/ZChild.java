package com.keenant.dhub.hub.plugins.zwave;

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
    private final ZDevice node;
    private final int endPoint;
    private final List<CmdClass> cmdClasses;
    private List<ZFeature> features;

    public ZChild(ZNode node, int endPoint, List<CmdClass> cmdClasses) {
        this.node = node;
        this.endPoint = endPoint;
        this.cmdClasses = cmdClasses;
    }

    public int getEndPoint() {
        return endPoint;
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
}
