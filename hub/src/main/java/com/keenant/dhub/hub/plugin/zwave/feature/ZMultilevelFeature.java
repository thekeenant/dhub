package com.keenant.dhub.hub.plugin.zwave.feature;

import com.keenant.dhub.hub.network.feature.IntegerFeature;
import com.keenant.dhub.hub.plugin.zwave.ZDevice;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd.Report;

import java.util.Optional;

public class ZMultilevelFeature extends IntegerFeature<ZDevice> {
    public ZMultilevelFeature(ZDevice device) {
        super(device);
    }

    @Override
    protected void send(Integer data) {
        getDevice().send(CmdClass.SWITCH_MULTILEVEL.set(data));
    }

    @Override
    public Optional<Integer> fetch() {
        return getDevice().send(CmdClass.SWITCH_MULTILEVEL.get()).map(Report::getCurrent);
    }
}
