package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import lombok.ToString;

@ToString(callSuper = true)
public class MemoryGetIdEvent extends IncomingMessageEvent<MemoryGetIdMsg.Response> {
    public MemoryGetIdEvent(Controller controller, MemoryGetIdMsg.Response message) {
        super(controller, message);
    }
}
