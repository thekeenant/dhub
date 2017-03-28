package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.ApplicationUpdateMsg;
import lombok.ToString;

@ToString(callSuper = true)
public class ApplicationUpdateEvent extends InboundMessageEvent<ApplicationUpdateMsg> {
    public ApplicationUpdateEvent(Controller controller, ApplicationUpdateMsg message) {
        super(controller, message);
    }
}
