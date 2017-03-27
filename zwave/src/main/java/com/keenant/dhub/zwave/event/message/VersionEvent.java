package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.messages.VersionMsg;
import lombok.ToString;

@ToString(callSuper = true)
public class VersionEvent extends IncomingMessageEvent<VersionMsg.Response> {
    public VersionEvent(Controller controller, VersionMsg.Response message) {
        super(controller, message);
    }
}
