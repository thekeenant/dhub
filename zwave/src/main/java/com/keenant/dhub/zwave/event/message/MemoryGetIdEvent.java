package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg.Response;
import lombok.ToString;

@ToString(callSuper = true)
public class MemoryGetIdEvent extends InboundMessageEvent<Response> {
    public MemoryGetIdEvent(Controller controller, Response message) {
        super(controller, message);
    }
}
