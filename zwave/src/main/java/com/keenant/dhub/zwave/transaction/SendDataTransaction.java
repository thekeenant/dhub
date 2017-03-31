package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg.Callback;
import com.keenant.dhub.zwave.messages.SendDataMsg.Reply;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = {"replyParser", "callbackParser"})
public class SendDataTransaction<T extends Cmd<R>, R extends InboundCmd> extends Transaction {
    private final SendDataMsg<T, R> message;
    private final MessageParser<Reply> replyParser;
    private final MessageParser<Callback> callbackParser;

    private State state;
    private Reply reply;
    private Callback callback;
    private R response;

    private enum State {
        SENT,
        RECEIVED_REPLY,
        RECEIVED_CALLBACK,
        DONE,
        FAILED
    }

    public SendDataTransaction(Controller controller, SendDataMsg<T, R> message, MessageParser<Reply> replyParser, MessageParser<Callback> callbackParser) {
        super(controller);
        this.message = message;
        this.replyParser = replyParser;
        this.callbackParser = callbackParser;
    }

    @Override
    public SendDataTransaction<T, R> await() {
        super.await();
        return this;
    }

    @Override
    public SendDataTransaction<T, R> await(int timeout) {
        super.await(timeout);
        return this;
    }

    public Optional<Reply> getReply() {
        return Optional.ofNullable(reply);
    }

    public Optional<Callback> getCallback() {
        return Optional.ofNullable(callback);
    }

    public Optional<R> getResponse() {
        return Optional.ofNullable(response);
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
        CmdParser<R> responseParser = message.getCmd().getResponseParser().orElse(null);

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
                state = responseParser == null ? State.DONE : State.RECEIVED_CALLBACK;
                return callback;

            case RECEIVED_CALLBACK:
                // These generics aren't safe...
                ApplicationCommandMsg<R> cmdMessage = ApplicationCommandMsg.<R>parse(msg).orElse(null);
                response = cmdMessage.getCmd();

                if (response == null) {
                    state = State.FAILED;
                    break;
                }

                state = State.DONE;
                return cmdMessage;

            default:
                state = State.FAILED;
                break;
        }

        return msg;
    }
}
