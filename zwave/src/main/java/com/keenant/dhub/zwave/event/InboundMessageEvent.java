package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import lombok.ToString;

@ToString
public abstract class InboundMessageEvent<T extends InboundMessage> extends ControllerEvent {
    private final T message;

    public InboundMessageEvent(Controller controller, T message) {
        super(controller);
        this.message = message;
    }

    public T getMessage() {
        return message;
    }
}
