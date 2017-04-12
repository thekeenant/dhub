package com.keenant.dhub.zwave.event.cmd;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.EndPoint;

/**
 * This command encapsulates another command, so listening to this particular event is generally not needed.
 * The command this command encapsulates gets it's own event.
 */
public class MultiChannelInboundEncapEvent extends CmdEvent<MultiChannelCmd.InboundEncap> {
    public MultiChannelInboundEncapEvent(Controller controller, EndPoint endPoint, MultiChannelCmd.InboundEncap cmd) {
        super(controller, endPoint, cmd);
    }
}
