package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import lombok.ToString;

import java.util.Optional;

/**
 * PC -> ZW: Request
 * ZW -> PC: ACK
 * ZW -> PC: Response/Request Callback
 * PC -> ZW: ACK
 *
 * @param <R>
 */
@ToString(exclude = "parser")
public class ReplyTransaction<R extends InboundMessage> extends Transaction {
    private final Message message;
    private final MessageParser<R> parser;
    private State state;
    private R reply;

    private enum State {
        SENT,
        WAITING,
        DONE,
        FAILED
    }

    public ReplyTransaction(Controller controller, Message message, MessageParser<R> parser) {
        super(controller);
        this.message = message;
        this.parser = parser;
        this.state = null;
        this.reply = null;
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
    public boolean isComplete() {
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
    public InboundMessage handle(UnknownMessage msg) {
        switch (state) {
            case WAITING:
                reply = parser.parseMessage(msg).orElse(null);
                if (reply == null) {
                    state = State.FAILED;
                    break;
                }

                state = State.DONE;
                return reply;
            default:
                state = State.FAILED;
                break;
        }

        return msg;
    }
}
