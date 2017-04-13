package com.keenant.dhub.hub.plugins.zwave.provider;

import com.keenant.dhub.hub.network.provider.IntegerProvider;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd.Report;

public class ZMultilevelProvider extends IntegerProvider {
    public ZMultilevelProvider(ZDevice device) {
        super(device, () -> device.send(CmdClass.SWITCH_MULTILEVEL.get()).map(Report::getCurrent));
    }
}
