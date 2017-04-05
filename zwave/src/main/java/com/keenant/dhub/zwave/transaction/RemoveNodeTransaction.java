package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.MessageParser;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg.Callback;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg.Mode;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg.State;
import lombok.ToString;

@ToString(callSuper = true)
public class RemoveNodeTransaction extends CallbackTransaction<Callback> {
    private static final long TIMEOUT = 60000;

    private boolean stopQueued;
    private boolean stopAckReceived;

    public RemoveNodeTransaction(Controller controller, Message<?> message, MessageParser<Callback> parser) {
        super(controller, message, parser);
    }

    @Override
    public RemoveNodeTransaction await() {
        super.await();
        return this;
    }

    @Override
    public RemoveNodeTransaction await(int timeout) {
        super.await(timeout);
        return this;
    }

    /**
     * Send the stop remove node data frame.
     * @throws IllegalStateException If the transaction hasn't started, or it has already finished.
     */
    public void stop() {
        if (!isStarted()) {
            throw new IllegalStateException("Remove node transaction not started.");
        }
        if (isComplete()) {
            throw new IllegalStateException("Remove node transaction already finished.");
        }

        queueStopMsg();
    }

    private void queueStopMsg() {
        stopQueued = true;
        addToOutboundQueue(new RemoveNodeMsg(Mode.STOP));
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
        System.out.println(latestCallback);

        if (stopQueued) {
            return stopAckReceived;
        }

        if (latestCallback != null) {
            boolean finished = latestCallback.getState() == State.DONE || latestCallback.getState() == State.FAILED;
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
