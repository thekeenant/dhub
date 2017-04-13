package com.keenant.dhub.hub.action;

import java.util.Arrays;
import java.util.List;

public class ActionSet implements Action {
    private final List<Action> actions;

    private Action current;
    private boolean stopped;

    public ActionSet(List<Action> actions) {
        this.actions = actions;
    }

    public ActionSet(Action... actions) {
        this(Arrays.asList(actions));
    }

    @Override
    public void execute() {
        stopped = false;
        for (Action action : actions) {
            if (stopped) {
                break;
            }
            current = action;
            action.execute();
        }
    }

    @Override
    public void stop() {
        stopped = true;
        if (current != null) {
            current.stop();
        }
    }
}
