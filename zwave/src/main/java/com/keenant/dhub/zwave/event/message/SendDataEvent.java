package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import lombok.ToString;

@ToString(callSuper = true)
public class SendDataEvent extends IncomingMessageEvent<SendDataMsg.Response> {
    public SendDataEvent(Controller controller, SendDataMsg.Response message) {
        super(controller, message);
    }
}
