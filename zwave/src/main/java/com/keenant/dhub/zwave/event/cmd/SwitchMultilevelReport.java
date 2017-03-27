package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

public class SwitchMultilevelReport extends CmdEvent<SwitchMultilevelCmd.Report> {
    public SwitchMultilevelReport(Controller controller, int nodeId, SwitchMultilevelCmd.Report cmd) {
        super(controller, nodeId, cmd);
    }
}
