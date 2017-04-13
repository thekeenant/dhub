package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.hub.network.provider.BooleanProvider;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;

public class ZSwitchBinaryProvider extends BooleanProvider {
    public ZSwitchBinaryProvider(ZDevice device) {
        super(device, () -> device.send(CmdClass.SWITCH_BINARY.get()).map(Report::getValue));
    }
}
