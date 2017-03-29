package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg.Reply;
import lombok.ToString;

@ToString(callSuper = true)
public class SendDataReplyEvent extends InboundMessageEvent<Reply> {
    public SendDataReplyEvent(Controller controller, Reply message) {
        super(controller, message);
    }
}
