package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.NodeListMsg.Reply;

public class NodeListReplyEvent extends InboundMessageEvent<Reply> {
    public NodeListReplyEvent(Controller controller, Reply message) {
        super(controller, message);
    }
}
