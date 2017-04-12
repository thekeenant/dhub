package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.DataMsg.Callback;
import lombok.ToString;

@ToString(callSuper = true)
public class DataCallbackEvent extends InboundMessageEvent<Callback> {
    public DataCallbackEvent(Controller controller, Callback message) {
        super(controller, message);
    }
}
