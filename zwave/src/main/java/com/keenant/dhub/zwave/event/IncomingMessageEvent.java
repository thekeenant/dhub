package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import lombok.ToString;

@ToString
public abstract class IncomingMessageEvent<T extends IncomingMessage> extends ControllerEvent {
    private final T message;

    protected IncomingMessageEvent(Controller controller, T message) {
        super(controller);
        this.message = message;
    }

    public T getMessage() {
        return message;
    }
}
