package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;
import lombok.ToString;

@ToString(callSuper = true)
public class SendDataEvent extends InboundMessageEvent<Response> {
    public SendDataEvent(Controller controller, SendDataMsg.Response message) {
        super(controller, message);
    }
}
