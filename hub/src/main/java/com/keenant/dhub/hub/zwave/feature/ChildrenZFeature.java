package com.keenant.dhub.hub.zwave.feature;

import com.keenant.dhub.hub.event.NetworkEvent;
import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.hub.network.feature.ChildrenFeature;
import com.keenant.dhub.hub.zwave.ZChild;
import com.keenant.dhub.hub.zwave.ZFeature;
import com.keenant.dhub.hub.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.CapabilityReport;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.EndPointReport;
import lombok.ToString;
import net.engio.mbassy.bus.MBassador;

import java.util.List;

@ToString(exclude = "node")
public class ChildrenZFeature extends ChildrenFeature<ZChild> implements ZFeature {
    private final ZNode node;
    private final MBassador<NetworkEvent> bus = new MBassador<>();

    public ChildrenZFeature(ZNode node) {
        this.node = node;
    }

    @Override
    public void start() {
        EndPointReport endpoints = node.send(CmdClass.MULTI_CHANNEL.endPointGet()).orElse(null);

        List<CmdClass> cmdClasses = null;

        for (int i = 0; i < endpoints.getEndPointCount(); i++) {
            int endPoint = i + 1;

            // Only fetch cmdClasses if we haven't before, or if each endpoint is different (not identical).
            if (!endpoints.isEndPointsIdentical() || cmdClasses == null) {
                CapabilityReport capabilities = node.send(CmdClass.MULTI_CHANNEL.capabilityGet(endPoint)).orElse(null);
                cmdClasses = capabilities.getCmdClasses();
            }

            ZChild child = new ZChild(node, this, endPoint, cmdClasses);
            getDevices().add(child);
        }

        getDevices().forEach(ZChild::start);
    }

    @Override
    public void stop() {
        getDevices().forEach(ZChild::stop);
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
}
