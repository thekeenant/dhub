package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.MessageParser;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.AddNodeMsg;
import com.keenant.dhub.zwave.messages.AddNodeMsg.Callback;
import com.keenant.dhub.zwave.messages.AddNodeMsg.Mode;
import com.keenant.dhub.zwave.messages.AddNodeMsg.State;
import lombok.ToString;

@ToString(callSuper = true)
public class AddNodeTransaction extends CallbackTransaction<Callback> {
    private static final long TIMEOUT = 60000;

    private boolean stopQueued;
    private boolean stopAckReceived;

    public AddNodeTransaction(Controller controller, Message<?> message, MessageParser<Callback> parser) {
        super(controller, message, parser);
    }

    /**
     * Send the stop remove node data frame.
     * @throws IllegalStateException If the transaction hasn't started, or it has already finished.
     */
    public void stop() {
        if (!isStarted()) {
            throw new IllegalStateException("Remove node transaction not started.");
        }
        if (isFinished()) {
            throw new IllegalStateException("Remove node transaction already finished.");
        }

        queueStopMsg();
    }

    private void queueStopMsg() {
        stopQueued = true;
        addToOutboundQueue(new AddNodeMsg(Mode.STOP));
    }

    @Override
    public void handle(Status status) {
        if (stopQueued) {
            if (status == Status.ACK) {
                stopAckReceived = true;
            }
            else {
                queueStopMsg();
            }
        }
    }

    @Override
    protected boolean isFinished(Callback latestCallback) {
        if (!isStarted()) {
            return false;
        }

        if (stopQueued) {
            return stopAckReceived;
        }

        if (latestCallback != null) {
            State state = latestCallback.getState();

            boolean finished = state == State.PROTOCOL_DONE || state == State.DONE || state == State.FAILED;
            if (finished) {
                queueStopMsg();
                return false;
            }
        }

        if (millisAlive() > TIMEOUT) {
            // We don't call stop(), because we'd have an infinite loop.
            queueStopMsg();
        }

        return false;
    }
}
