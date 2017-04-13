package com.keenant.dhub.hub.action;

import java.util.function.Supplier;

public class LogAction implements Action {
    private final Supplier<String> message;

    public LogAction(Supplier<String> message) {
        this.message = message;
    }

    public LogAction(String message) {
        this(() -> message);
    }

    @Override
    public void execute() {
        System.out.println(message.get());
    }

    @Override
    public void stop() {

    }
}
