package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.DataMsg.Reply;
import lombok.ToString;

@ToString(callSuper = true)
public class DataReplyEvent extends InboundMessageEvent<Reply> {
    public DataReplyEvent(Controller controller, Reply message) {
        super(controller, message);
    }
}
