package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg.Reply;
import lombok.ToString;

@ToString(callSuper = true)
public class MemoryGetIdReplyEvent extends InboundMessageEvent<Reply> {
    public MemoryGetIdReplyEvent(Controller controller, Reply message) {
        super(controller, message);
    }
}
