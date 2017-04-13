package com.keenant.dhub.hub.action;

public class WaitAction implements Action {
    private final int seconds;

    private boolean stopped;

    public WaitAction(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void execute() {
        stopped = false;

        long start = System.currentTimeMillis();
        while (!stopped) {
            long curr = System.currentTimeMillis();

            if (curr - start > seconds * 1000) {
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
