package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg.Callback;
import lombok.ToString;

@ToString(callSuper = true)
public class RemoveNodeCallbackEvent extends InboundMessageEvent<Callback> {
    public RemoveNodeCallbackEvent(Controller controller, Callback message) {
        super(controller, message);
    }
}
