package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.Message;

public class ReqTransaction extends Transaction {
    private final Message message;
    private boolean done;

    public ReqTransaction(ZController controller, Message message, Priority priority) {
        super(controller, priority);
        this.message = message;
    }

    public ReqTransaction(ZController controller, Message message) {
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
    public IncomingDataFrame handle(IncomingDataFrame frame) {
        return frame;
    }
}
