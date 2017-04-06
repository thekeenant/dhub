package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

public class MultiChannelInboundEncapEvent extends CmdEvent<MultiChannelCmd.InboundEncap> {
    public MultiChannelInboundEncapEvent(Controller controller, int nodeId, MultiChannelCmd.InboundEncap cmd) {
        super(controller, nodeId, cmd);
    }
}
