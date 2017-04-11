package com.keenant.dhub.hub.zwave.feature;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.feature.BinaryFeature;
import com.keenant.dhub.hub.zwave.ZChild;
import com.keenant.dhub.hub.zwave.ZDevice;
import com.keenant.dhub.hub.zwave.ZFeature;
import com.keenant.dhub.hub.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.ControllerListener;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;
import com.keenant.dhub.zwave.event.cmd.MultiChannelInboundEncapEvent;
import com.keenant.dhub.zwave.event.cmd.SwitchBinaryReportEvent;
import lombok.ToString;
import net.engio.mbassy.listener.Handler;

import java.util.Optional;

@ToString(exclude = "device")
public class BinaryZFeature extends BinaryFeature implements ZFeature, ControllerListener {
    private final ZDevice device;
    private Boolean latestValue;

    public BinaryZFeature(ZDevice device) {
        this.device = device;
    }

    @Override
    public Optional<Boolean> getState() {
        return Optional.ofNullable(latestValue);
    }

    @Override
    public void updateState() {
        latestValue = device.send(CmdClass.SWITCH_BINARY.get())
                .map(Report::getValue)
                .orElse(null);

    }

    @Override
    public void setState(boolean state) {
        device.send(CmdClass.SWITCH_BINARY.set(state));
        updateState();
    }

    @Override
    public void start() {
        device.getController().subscribe(this);
        updateState();
    }

    @Handler
    public void onBinaryReport(SwitchBinaryReportEvent event) {
        System.out.println("Got a report");
        if (device instanceof ZNode) {
            ZNode node = (ZNode) device;
            if (node.getId() == event.getNodeId()) {
                latestValue = event.getCmd().getValue();
                publishFeatureChange();
            }
        }
    }

    @Handler
    public void onBinaryReport(MultiChannelInboundEncapEvent event) {
        if (!(event.getCmd().getCmd() instanceof Report)) {
            return;
        }

        Report report = (Report) event.getCmd().getCmd();

        System.out.println("Got a report");
        if (device instanceof ZChild) {
            ZChild child = (ZChild) device;
            if (child.getNode().getId() == event.getNodeId() && child.getEndPoint() == event.getCmd().getEndPoint()) {
                latestValue = report.getValue();
                publishFeatureChange();
            }
        }
    }

    @Override
    public void stop() {
        device.getController().unsubscribe(this);
    }

    @Override
    public Device getDevice() {
        return device;
    }
}
