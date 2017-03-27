package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

public class SwitchBinaryReportEvent extends CmdEvent<SwitchBinaryCmd.Report> {
    public SwitchBinaryReportEvent(Controller controller, int nodeId, SwitchBinaryCmd.Report cmd) {
        super(controller, nodeId, cmd);
    }
}
