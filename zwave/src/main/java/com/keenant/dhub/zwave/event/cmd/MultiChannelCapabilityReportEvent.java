package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

public class MultiChannelCapabilityReportEvent extends CmdEvent<MultiChannelCmd.CapabilityReport> {
    public MultiChannelCapabilityReportEvent(Controller controller, int nodeId, MultiChannelCmd.CapabilityReport cmd) {
        super(controller, nodeId, cmd);
    }
}
