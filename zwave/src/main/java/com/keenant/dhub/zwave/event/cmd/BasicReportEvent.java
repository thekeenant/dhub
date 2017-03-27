package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

public class BasicReportEvent extends CmdEvent<BasicCmd.Report> {
    public BasicReportEvent(Controller controller, int nodeId, BasicCmd.Report cmd) {
        super(controller, nodeId, cmd);
    }
}
