package com.keenant.dhub.hub.action;

public class RepeatAction implements Action {
    private final int times;
    private final Action action;

    public RepeatAction(int times, Action action) {
        this.times = times;
        this.action = action;
    }

    @Override
    public void execute() {
        for (int i = 0; i < times; i++) {
            action.execute();
        }
    }

    @Override
    public void stop() {
        action.stop();
    }
}
