package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.IncomingMessage;
import lombok.ToString;

@ToString
public abstract class IncomingMessageEvent<T extends IncomingMessage> extends ControllerEvent {
    private final T message;

    public IncomingMessageEvent(ZController controller, T message) {
        super(controller);
        this.message = message;
    }

    public T getMessage() {
        return message;
    }
}
