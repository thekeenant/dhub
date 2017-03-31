package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg;
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
        RECEIVED_RES,
        DONE,
        FAILED
    }

    public ReplyCallbackTransaction(Controller controller, Message<?> message, MessageParser<R> replyParser, MessageParser<C> callbackParser) {
        super(controller);
        this.message = message;
        this.replyParser = replyParser;
        this.callbackParser = callbackParser;
    }

    public Optional<R> getReply() {
        return Optional.ofNullable(reply);
    }

    @Override
    public void start() {
        addToOutboundQueue(message);
        state = State.SENT;
    }

    @Override
    public boolean isFinished() {
        return getOutboundQueue().isEmpty() && (state == State.DONE || state == State.FAILED);
    }

    @Override
    public void handle(Status status) {
        switch (state) {
            case SENT:
                state = status == Status.ACK ? State.WAITING : State.FAILED;
                break;
            default:
                state = State.FAILED;
                break;
        }
    }

    @Override
    public InboundMessage handle(UnknownMessage frame) {
        switch (state) {
            case WAITING:
                reply = replyParser.parseMessage(frame).orElse(null);

                if (reply == null) {
                    state = State.FAILED;
                    break;
                }

                state = State.RECEIVED_RES;
                return reply;
            case RECEIVED_RES:
                callback = callbackParser.parseMessage(frame).orElse(null);

                if (callback == null) {
                    state = State.FAILED;
                    break;
                }

                state = State.DONE;
                return callback;
            default:
                getController().send(message, Priority.HIGHEST);
                state = State.FAILED;
                break;
        }

        return frame;
    }
}
