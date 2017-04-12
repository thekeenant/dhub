package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.EndPoint;

public class SwitchMultilevelReport extends CmdEvent<SwitchMultilevelCmd.Report> {
    public SwitchMultilevelReport(Controller controller, EndPoint endPoint, SwitchMultilevelCmd.Report cmd) {
        super(controller, endPoint, cmd);
    }
}
