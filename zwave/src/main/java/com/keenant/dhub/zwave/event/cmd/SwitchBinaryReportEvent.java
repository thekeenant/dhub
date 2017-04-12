package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.EndPoint;

public class SwitchBinaryReportEvent extends CmdEvent<SwitchBinaryCmd.Report> {
    public SwitchBinaryReportEvent(Controller controller, EndPoint endPoint, SwitchBinaryCmd.Report cmd) {
        super(controller, endPoint, cmd);
    }
}
