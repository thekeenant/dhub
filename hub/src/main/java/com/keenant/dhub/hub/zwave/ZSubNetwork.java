package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.hub.event.NetworkEvent;
import com.keenant.dhub.hub.network.DeviceCollection;
import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.hub.network.SubNetwork;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.CapabilityReport;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.EndPointReport;
import net.engio.mbassy.bus.MBassador;

import java.util.List;

public class ZSubNetwork implements SubNetwork {
    private final ZNode parentNode;
    private final DeviceCollection<ZChild> devices = new DeviceCollection<>();
    private final MBassador<NetworkEvent> bus = new MBassador<>();

    public ZSubNetwork(ZNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public void start() {
        devices.clear();

        EndPointReport endpoints = parentNode.send(CmdClass.MULTI_CHANNEL.endPointGet()).orElse(null);

        List<CmdClass> cmdClasses = null;

        for (int i = 0; i < endpoints.getEndPointCount(); i++) {
            int endPoint = i + 1;

            // Only fetch cmdClasses if we haven't before, or if each endpoint is different (not identical).
            if (!endpoints.isEndPointsIdentical() || cmdClasses == null) {
                CapabilityReport capabilities = parentNode.send(CmdClass.MULTI_CHANNEL.capabilityGet(endPoint)).orElse(null);
                cmdClasses = capabilities.getCmdClasses();
            }

            ZChild child = new ZChild(this, endPoint, cmdClasses);
            devices.add(child);
        }

        devices.forEach(ZChild::start);
    }

    @Override
    public void stop() {
        getDevices().forEach(ZChild::stop);
    }

    @Override
    public DeviceCollection<ZChild> getDevices() {
        return devices;
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

    @Override
    public ZNode getDevice() {
        return parentNode;
    }
}
