package com.keenant.dhub.hub.plugin.zwave.provider;

import com.keenant.dhub.hub.network.provider.BooleanProvider;
import com.keenant.dhub.hub.plugin.zwave.ZDevice;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;

public class ZBinaryProvider extends BooleanProvider {
    public ZBinaryProvider(ZDevice device) {
        super(device, () -> device.send(CmdClass.SWITCH_BINARY.get()).map(Report::getValue));
    }
}
