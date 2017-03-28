package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

public class MultiChannelEndPointReportEvent extends CmdEvent<MultiChannelCmd.EndPointReport> {
    public MultiChannelEndPointReportEvent(Controller controller, int nodeId, MultiChannelCmd.EndPointReport cmd) {
        super(controller, nodeId, cmd);
    }
}
