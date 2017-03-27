package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import com.keenant.dhub.zwave.frame.Frame;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.frame.UnknownDataFrame;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class Transaction {
    private final Controller controller;
    private final Queue<Frame> outgoing;
    private long queuedTimeNanos;
    private long startTimeNanos;
    private long completionTimeNanos;

    public Transaction(Controller controller) {
        this.controller = controller;
        this.outgoing = new ArrayDeque<>();
    }

    public abstract void start();

    public abstract boolean isFinished();

    public abstract IncomingMessage handle(UnknownDataFrame frame);

    public abstract void handle(Status status);

    public boolean isStarted() {
        return startTimeNanos > 0;
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

    /**
     * @return The time in nanoseconds from when the transaction started to when it ended.
     *         If it is in progress, it is the time up until present time.
     * @throws UnsupportedOperationException If the transaction hasn't started.
     */
    public long nanosAlive() throws UnsupportedOperationException {
        if (startTimeNanos <= 0) {
            throw new UnsupportedOperationException("Transaction has not started yet.");
        }

        long end = getCompletionTimeNanos();

        if (getCompletionTimeNanos() <= 0) {
            end = System.nanoTime();
        }

        return end - startTimeNanos;
    }

    protected void addToOutgoingQueue(Frame frame) {
        outgoing.add(frame);
    }

    public Queue<Frame> getOutgoingQueue() {
        return outgoing;
    }

    public Controller getController() {
        return controller;
    }

    public long getCompletionTimeNanos() {
        return completionTimeNanos;
    }

    public long getStartTimeNanos() {
        return startTimeNanos;
    }

    public long getQueuedTimeNanos() {
        return queuedTimeNanos;
    }

    public void setCompletionTimeNanos(long completionTimeNanos) {
        this.completionTimeNanos = completionTimeNanos;
    }

    public void setStartTimeNanos(long startTimeNanos) {
        this.startTimeNanos = startTimeNanos;
    }

    public void setQueuedTimeNanos(long queuedTimeNanos) {
        this.queuedTimeNanos = queuedTimeNanos;
    }
}
