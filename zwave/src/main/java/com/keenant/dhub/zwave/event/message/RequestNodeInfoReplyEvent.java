package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg.Reply;

public class RequestNodeInfoReplyEvent extends InboundMessageEvent<Reply> {
    public RequestNodeInfoReplyEvent(Controller controller, Reply message) {
        super(controller, message);
    }
}
