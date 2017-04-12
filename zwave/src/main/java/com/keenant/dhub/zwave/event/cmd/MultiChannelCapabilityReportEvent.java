package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.EndPoint;

public class MultiChannelCapabilityReportEvent extends CmdEvent<MultiChannelCmd.CapabilityReport> {
    public MultiChannelCapabilityReportEvent(Controller controller, EndPoint endPoint, MultiChannelCmd.CapabilityReport cmd) {
        super(controller, endPoint, cmd);
    }
}
