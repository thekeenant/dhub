package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.frame.Frame;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.frame.Status;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;

public abstract class Transaction {
    private final Priority priority;
    private final Queue<Frame> outgoing;
    private final Date creationTime;

    public Transaction(Priority priority) {
        this.priority = priority;
        this.outgoing = new ArrayDeque<>();
        this.creationTime = new Date();
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void await() {
        while (!isFinished()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
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
}
