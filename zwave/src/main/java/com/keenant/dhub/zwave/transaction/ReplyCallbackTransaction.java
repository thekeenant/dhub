package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import lombok.ToString;

import java.util.Optional;

/**
 * PC -> ZW: Request
 * ZW -> PC: ACK
 * ZW -> PC: Response
 * PC -> ZW: ACK
 *   ...
 * ZW -> PC: Callback
 * PC -> ZW: ACK
 *
 * @param <R> The reply type.
 */
@ToString(exclude = {"replyParser", "callbackParser"})
public class ReplyCallbackTransaction<R extends InboundMessage, C extends InboundMessage> extends Transaction {
    private final Message<?> message;
    private final MessageParser<R> replyParser;
    private final MessageParser<C> callbackParser;
    private State state;
    private R reply;
    private C callback;

    private enum State {
        SENT,
        WAITING,
        RECEIVED_REPLY,
        DONE,
        FAILED
    }

    public ReplyCallbackTransaction(Controller controller, Message<?> message, MessageParser<R> replyParser, MessageParser<C> callbackParser) {
        super(controller);
        this.message = message;
        this.replyParser = replyParser;
        this.callbackParser = callbackParser;
    }

    @Override
    public ReplyCallbackTransaction<R, C> await() {
        super.await();
        return this;
    }

    @Override
    public ReplyCallbackTransaction<R, C> await(int timeout) {
        super.await(timeout);
        return this;
    }

    public Optional<R> getReply() {
        return Optional.ofNullable(reply);
    }

    public Optional<C> getCallback() {
        return Optional.ofNullable(callback);
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
            case WAITING:
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

                state = State.DONE;
                return callback;

            default:
                state = State.FAILED;
                break;
        }

        return msg;
    }
}
