package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.frame.Frame;
import com.keenant.dhub.zwave.frame.Status;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public abstract class Transaction {
    private final Controller controller;
    private final Queue<Frame> outbound;
    private long queuedTimeNanos;
    private long startTimeNanos;
    private long completionTimeNanos;

    public Transaction(Controller controller) {
        this.controller = controller;
        this.outbound = new ArrayDeque<>();
    }

    public abstract void start();

    public abstract boolean isComplete();

    public abstract InboundMessage handle(UnknownMessage msg);

    public abstract void handle(Status status);

    public boolean isStarted() {
        return startTimeNanos > 0;
    }

    public void await() {
        await(-1);
    }

    public boolean await(int timeout) {
        long start = System.currentTimeMillis();
        while (!isComplete()) {
            long now = System.currentTimeMillis();

            if (!controller.isAlive()) {
                return false;
            }

            if (timeout > 0 && now - start > timeout) {
                return false;
            }

            try {
                Thread.sleep(50);
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

    /**
     * @return The time in milliseconds from when the transaction started to when it ended.
     *         If it is in progress, it is the time up until present time.
     * @throws UnsupportedOperationException If the transaction hasn't started.
     */
    public long millisAlive() throws UnsupportedOperationException {
        long nanos = nanosAlive();
        return TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
    }

    protected void addToOutboundQueue(Frame frame) {
        outbound.add(frame);
    }

    public Queue<Frame> getOutboundQueue() {
        return outbound;
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
