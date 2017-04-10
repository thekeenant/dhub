package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.feature.ChildrenFeature;
import com.keenant.dhub.hub.plugins.zwave.ZChild;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.CapabilityReport;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.EndPointReport;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(exclude = "node")
public class ChildrenZFeature extends ChildrenFeature implements ZFeature {
    private final ZNode node;
    private List<ZChild> children;

    public ChildrenZFeature(ZNode node) {
        this.node = node;
    }

    @Override
    public void start() {
        children = new ArrayList<>();

        EndPointReport endpoints = node.send(CmdClass.MULTI_CHANNEL.endPointGet()).orElse(null);

        List<CmdClass> cmdClasses = null;

        for (int i = 0; i < endpoints.getEndPointCount(); i++) {
            int endPoint = i + 1;

            // Only fetch cmdClasses if we haven't before, or if each endpoint is different (not identical).
            if (!endpoints.isEndPointsIdentical() || cmdClasses == null) {
                CapabilityReport capabilities = node.send(CmdClass.MULTI_CHANNEL.capabilityGet(endPoint)).orElse(null);
                cmdClasses = capabilities.getCmdClasses();
            }

            ZChild child = new ZChild(node, endPoint, cmdClasses);
            children.add(child);
        }

        children.forEach(ZChild::start);
    }

    @Override
    public void stop() {
        children.forEach(ZChild::stop);
    }

    @Override
    public List<ZChild> getChildren() {
        return children;
    }
}
