package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.messages.InitDataMsg;
import lombok.ToString;

@ToString(callSuper = true)
public class InitDataEvent extends IncomingMessageEvent<InitDataMsg.Response> {
    public InitDataEvent(Controller controller, InitDataMsg.Response message) {
        super(controller, message);
    }
}
