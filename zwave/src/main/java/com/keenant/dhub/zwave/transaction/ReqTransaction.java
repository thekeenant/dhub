package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.frame.Status;

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
        addToOutboundQueue(message);
    }

    @Override
    public boolean isFinished() {
        return getOutboundQueue().isEmpty() && done;
    }

    @Override
    public void handle(Status status) {
        done = true;
    }

    @Override
    public InboundMessage handle(UnknownMessage frame) {
        return frame;
    }
}
