package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.frame.UnknownDataFrame;

public class ReqTransaction extends Transaction {
    private final Message message;
    private boolean done;

    public ReqTransaction(Controller controller, Message message, Priority priority) {
        super(controller, priority);
        this.message = message;
    }

    public ReqTransaction(Controller controller, Message message) {
        this(controller, message, Priority.DEFAULT);
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
    public IncomingMessage handle(UnknownDataFrame frame) {
        return frame;
    }
}
