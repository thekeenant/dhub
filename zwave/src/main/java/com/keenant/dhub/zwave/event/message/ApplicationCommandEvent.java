package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import lombok.ToString;

@ToString(callSuper = true)
public class ApplicationCommandEvent extends IncomingMessageEvent<ApplicationCommandMsg> {
    public ApplicationCommandEvent(Controller controller, ApplicationCommandMsg message) {
        super(controller, message);
    }
}
