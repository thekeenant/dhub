package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(exclude = "parser")
public abstract class CallbackTransaction<C extends InboundMessage> extends Transaction {
    private final Message<?> message;
    private final MessageParser<C> parser;
    private List<C> callbacks;
    private boolean failed;

    public CallbackTransaction(Controller controller, Message<?> message, MessageParser<C> parser) {
        super(controller);
        this.message = message;
        this.parser = parser;
    }

    public List<C> getCallbacks() {
        return callbacks;
    }

    protected abstract boolean isFinished(C latestCallback);

    @Override
    public void start() {
        callbacks = new ArrayList<>();
        addToOutboundQueue(message);
    }

    @Override
    public boolean isFinished() {
        if (!isStarted()) {
            return false;
        }

        if (!getOutboundQueue().isEmpty()) {
            return false;
        }

        if (failed) {
            return true;
        }

        C lastCallback = callbacks.isEmpty() ? null : callbacks.get(callbacks.size() - 1);
        return isFinished(lastCallback);
    }

    @Override
    public InboundMessage handle(UnknownMessage frame) {
        C callback = parser.parseMessage(frame).orElse(null);
        if (callback == null) {
            failed = true;
            return frame;
        }
        else {
            callbacks.add(callback);
            return callback;
        }
    }

    @Override
    public void handle(Status status) {
        // Nothing to do
    }
}
