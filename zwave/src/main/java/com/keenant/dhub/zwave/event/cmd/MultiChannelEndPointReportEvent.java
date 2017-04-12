package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.EndPoint;

public class MultiChannelEndPointReportEvent extends CmdEvent<MultiChannelCmd.EndPointReport> {
    public MultiChannelEndPointReportEvent(Controller controller, EndPoint endPoint, MultiChannelCmd.EndPointReport cmd) {
        super(controller, endPoint, cmd);
    }
}
