package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.EndPoint;

public class BasicReportEvent extends CmdEvent<BasicCmd.Report> {
    public BasicReportEvent(Controller controller, EndPoint endPoint, BasicCmd.Report cmd) {
        super(controller, endPoint, cmd);
    }
}
