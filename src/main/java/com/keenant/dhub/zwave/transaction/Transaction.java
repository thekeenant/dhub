package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.frame.Frame;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.frame.Status;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class Transaction {
    private final ZController controller;
    private final Priority priority;
    private final Queue<Frame> outgoing;
    private final long creationTimeNanos;
    private long startTimeNanos;
    private long completionTimeNanos;

    public Transaction(ZController controller, Priority priority) {
        this.controller = controller;
        this.priority = priority;
        this.outgoing = new ArrayDeque<>();
        this.creationTimeNanos = System.nanoTime();
    }

    public void await() {
        await(-1);
    }

    public boolean await(int timeout) {
        long start = System.currentTimeMillis();
        while (!isFinished()) {
            long now = System.currentTimeMillis();

            if (timeout > 0 && (!controller.isAlive() || now - start > timeout)) {
                return false;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }

        return true;
    }

    public Queue<Frame> getOutgoing() {
        return outgoing;
    }

    public abstract void start();

    public abstract boolean isFinished();

    public abstract IncomingDataFrame handle(IncomingDataFrame frame);

    public abstract void handle(Status status);

    public void queue(Frame frame) {
        outgoing.add(frame);
    }

    public Priority getPriority() {
        return priority;
    }

    public ZController getController() {
        return controller;
    }

    public long getCreationTimeNanos() {
        return creationTimeNanos;
    }

    public long getCompletionTimeNanos() {
        return completionTimeNanos;
    }

    public void setCompletionTimeNanos(long completionTimeNanos) {
        this.completionTimeNanos = completionTimeNanos;
    }

    public long getNanosAlive() {
        if (startTimeNanos <= 0) {
            throw new UnsupportedOperationException("Transaction has not started yet.");
        }

        long end = getCompletionTimeNanos();

        if (getCompletionTimeNanos() <= 0) {
            end = System.nanoTime();
        }

        return end - startTimeNanos;
    }

    public long getStartTimeNanos() {
        return startTimeNanos;
    }

    public void setStartTimeNanos(long startTimeNanos) {
        this.startTimeNanos = startTimeNanos;
    }
}
