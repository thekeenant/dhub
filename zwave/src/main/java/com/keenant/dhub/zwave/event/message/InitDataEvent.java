package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.InitDataMsg.Response;
import lombok.ToString;

@ToString(callSuper = true)
public class InitDataEvent extends InboundMessageEvent<Response> {
    public InitDataEvent(Controller controller, Response message) {
        super(controller, message);
    }
}
