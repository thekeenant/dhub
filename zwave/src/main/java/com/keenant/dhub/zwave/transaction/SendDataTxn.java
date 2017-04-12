package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.DataMsg.Callback;
import com.keenant.dhub.zwave.messages.DataMsg.Reply;
import com.keenant.dhub.zwave.messages.DataMsg.SendDataMsg;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = {"replyParser", "callbackParser"})
public class SendDataTxn<C extends Cmd> extends Transaction {
    private final SendDataMsg<C> message;
    private final MessageParser<Reply> replyParser;
    private final MessageParser<Callback> callbackParser;

    private State state;
    private Reply reply;
    private Callback callback;

    private enum State {
        SENT,
        RECEIVED_REPLY,
        DONE,
        FAILED
    }

    public SendDataTxn(Controller controller, SendDataMsg<C> message, MessageParser<Reply> replyParser, MessageParser<Callback> callbackParser) {
        super(controller);
        this.message = message;
        this.replyParser = replyParser;
        this.callbackParser = callbackParser;
    }

    @Override
    public SendDataTxn<C> await() {
        super.await();
        return this;
    }

    @Override
    public SendDataTxn<C> await(int timeout) {
        super.await(timeout);
        return this;
    }

    public Optional<Reply> getReply() {
        return Optional.ofNullable(reply);
    }

    public Callback getCallback() {
        return callback;
    }

    @Override
    public void start() {
        addToOutboundQueue(message);
        state = State.SENT;
    }

    @Override
    public boolean isComplete() {
        return getOutboundQueue().isEmpty() && (state == State.DONE || state == State.FAILED);
    }

    @Override
    public void handle(Status status) {
        // Todo
    }

    @Override
    public InboundMessage handle(UnknownMessage msg) {
        switch (state) {
            case SENT:
                reply = replyParser.parseMessage(msg).orElse(null);

                if (reply == null) {
                    state = State.FAILED;
                    break;
                }

                state = State.RECEIVED_REPLY;
                return reply;

            case RECEIVED_REPLY:
                callback = callbackParser.parseMessage(msg).orElse(null);

                if (callback == null) {
                    state = State.FAILED;
                    break;
                }

                // Move to done state if we don't expect a response, otherwise, we wait for more
                state = State.DONE;
                return callback;

            default:
                state = State.FAILED;
                break;
        }

        return msg;
    }
}
