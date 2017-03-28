package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.VersionMsg.Response;
import lombok.ToString;

@ToString(callSuper = true)
public class VersionEvent extends InboundMessageEvent<Response> {
    public VersionEvent(Controller controller, Response message) {
        super(controller, message);
    }
}
