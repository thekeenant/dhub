package com.keenant.dhub.hub.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcurrentActionSet implements Action {
    private final List<Action> actions;

    public ConcurrentActionSet(List<Action> actions) {
        this.actions = actions;
    }

    public ConcurrentActionSet(Action... actions) {
        this(Arrays.asList(actions));
    }

    @Override
    public void execute() {
        Map<Action, Boolean> completed = new HashMap<>();

        for (Action action : actions) {
            completed.put(action, false);

            new Thread(() -> {
                action.execute();
                completed.put(action, true);
            }).start();
        }

        // Wait until all actions are completed.
        while (completed.containsValue(false)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void stop() {
        for (Action action : actions) {
            action.stop();
        }
    }
}
