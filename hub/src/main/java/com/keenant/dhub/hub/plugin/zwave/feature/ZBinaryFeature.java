package com.keenant.dhub.hub.plugin.zwave.feature;

import com.keenant.dhub.hub.network.feature.BooleanFeature;
import com.keenant.dhub.hub.plugin.zwave.ZDevice;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;

import java.util.Optional;

public class ZBinaryFeature extends BooleanFeature<ZDevice> {
    public ZBinaryFeature(ZDevice device) {
        super(device);
    }

    @Override
    public Optional<Boolean> fetch() {
        return getDevice().send(CmdClass.SWITCH_BINARY.get()).map(Report::getValue);
    }

    @Override
    protected void send(Boolean data) {
        getDevice().send(CmdClass.SWITCH_BINARY.set(data));
    }
}
