package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.VersionMsg.Reply;
import lombok.ToString;

@ToString(callSuper = true)
public class VersionReplyEvent extends InboundMessageEvent<Reply> {
    public VersionReplyEvent(Controller controller, Reply message) {
        super(controller, message);
    }
}
