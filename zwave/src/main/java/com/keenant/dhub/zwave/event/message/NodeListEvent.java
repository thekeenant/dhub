package com.keenant.dhub.zwave.event.message;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.messages.NodeListMsg.Response;

public class NodeListEvent extends InboundMessageEvent<Response> {
    public NodeListEvent(Controller controller, Response message) {
        super(controller, message);
    }
}
