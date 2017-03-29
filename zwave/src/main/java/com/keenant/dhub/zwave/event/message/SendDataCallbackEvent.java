package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg.Callback;
import lombok.ToString;

@ToString(callSuper = true)
public class SendDataCallbackEvent extends InboundMessageEvent<Callback> {
    public SendDataCallbackEvent(Controller controller, Callback message) {
        super(controller, message);
    }
}
