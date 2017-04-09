package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.feature.BinaryGetFeature;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.event.cmd.SwitchBinaryReportEvent;
import net.engio.mbassy.listener.Handler;

public class BinaryGetZFeature implements BinaryGetFeature, ZFeature {
    private final ZDevice device;
    private boolean latestValue;

    public BinaryGetZFeature(ZDevice device) {
        this.device = device;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean getValue() {
        return latestValue;
    }

    @Override
    public void requestUpdate() {
        device.send(CmdClass.SWITCH_BINARY.get());
    }

    @Handler
    public void onBinaryGetCmd(SwitchBinaryReportEvent event) {
        if (device.getNodeId() == event.getNodeId()) {
            latestValue = event.getCmd().getValue();
        }
    }
}
