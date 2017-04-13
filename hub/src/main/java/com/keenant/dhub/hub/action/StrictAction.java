package com.keenant.dhub.hub.action;

import java.util.function.Supplier;

/**
 * An action that will repeat until sanityCheck resolves to true.
 */
public class StrictAction implements Action {
    private final Action action;
    private final Supplier<Boolean> sanityCheck;

    private boolean stopped;

    public StrictAction(Action action, Supplier<Boolean> sanityCheck) {
        this.action = action;
        this.sanityCheck = sanityCheck;
    }

    @Override
    public void execute() {
        stopped = false;

        while (!stopped) {
            action.execute();

            if (sanityCheck.get()) {
                break;
            }
        }
    }

    @Override
    public void stop() {
        stopped = true;
        action.stop();
    }
}
