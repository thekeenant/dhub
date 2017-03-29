package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.AddNodeMsg.Callback;
import lombok.ToString;

@ToString(callSuper = true)
public class AddNodeCallbackEvent extends InboundMessageEvent<Callback> {
    public AddNodeCallbackEvent(Controller controller, Callback message) {
        super(controller, message);
    }
}
