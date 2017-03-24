package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.Message;

import java.util.Optional;

public class ReqTransaction<Res extends IncomingDataFrame> extends Transaction {
    private final Message<Res> message;
    private boolean done;

    public ReqTransaction(Message<Res> message, Priority priority) {
        super(priority);
        this.message = message;
    }

    public ReqTransaction(Message<Res> message) {
        this(message, Priority.MEDIUM);
    }

    @Override
    public void start() {
        queue(message);
    }

    @Override
    public boolean isFinished() {
        return getOutgoing().isEmpty() && done;
    }

    @Override
    public void handle(Status status) {
        done = true;
    }

    @Override
    public IncomingDataFrame handle(IncomingDataFrame frame) {
        return frame;
    }
}
