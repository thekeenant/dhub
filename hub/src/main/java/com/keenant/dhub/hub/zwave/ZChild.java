package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.InboundEncap;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString(exclude = "network")
public class ZChild implements ZDevice {
    private final ZSubNetwork network;
    private final int endPoint;
    private final List<CmdClass> cmdClasses;
    private List<ZFeature> features;

    public ZChild(ZSubNetwork network, int endPoint, List<CmdClass> cmdClasses) {
        this.network = network;
        this.endPoint = endPoint;
        this.cmdClasses = cmdClasses;
    }

    public ZNode getNode() {
        return network.getDevice();
    }

    public int getEndPoint() {
        return endPoint;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public Optional<Network> getSubNetwork() {
        return Optional.empty();
    }

    @Override
    public Controller getController() {
        return network.getDevice().getController();
    }

    @Override
    public <C extends Cmd<R>, R extends InboundCmd> Optional<R> send(C cmd, int timeout) {
        Optional<InboundEncap<R>> encap = network.getDevice().send(CmdClass.MULTI_CHANNEL.encap(endPoint, cmd), timeout);
        return encap.map(InboundEncap::getCmd);
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
