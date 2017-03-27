package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.frame.UnknownDataFrame;

/**
 * PC -> ZW: Request
 * ZW -> PC: ACK
 * ZW -> PC: Response
 * PC -> ZW: ACK
 */
public class ReqTransaction extends Transaction {
    private final Message message;
    private boolean done;

    public ReqTransaction(Controller controller, Message message) {
        super(controller);
        this.message = message;
    }

    @Override
    public void start() {
        addToOutgoingQueue(message);
    }

    @Override
    public boolean isFinished() {
        return getOutgoingQueue().isEmpty() && done;
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
