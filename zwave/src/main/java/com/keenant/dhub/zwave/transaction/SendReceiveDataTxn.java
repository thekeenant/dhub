package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.messages.DataMsg.Callback;
import com.keenant.dhub.zwave.messages.DataMsg.Reply;
import com.keenant.dhub.zwave.messages.DataMsg.SendReceiveDataMsg;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString
public class SendReceiveDataTxn<C extends ResponsiveCmd<R>, R extends InboundCmd> extends Transaction {
    private final SendReceiveDataMsg<C, R> message;
    private final MessageParser<Reply> replyParser;
    private final MessageParser<Callback> callbackParser;

    private State state;
    private Reply reply;
    private List<Callback> callbacks = new ArrayList<>();
    private R response;

    private enum State {
        SENT,
        RECEIVED_REPLY,
        RECEIVED_CALLBACK,
        DONE,
        FAILED
    }

    public SendReceiveDataTxn(Controller controller, SendReceiveDataMsg<C, R> message, MessageParser<Reply> replyParser, MessageParser<Callback> callbackParser) {
        super(controller);
        this.message = message;
        this.replyParser = replyParser;
        this.callbackParser = callbackParser;
    }

    @Override
    public SendReceiveDataTxn<C, R> await() {
        super.await();
        return this;
    }

    @Override
    public SendReceiveDataTxn<C, R> await(int timeout) {
        super.await(timeout);
        return this;
    }

    public Optional<Reply> getReply() {
        return Optional.ofNullable(reply);
    }

    public List<Callback> getCallbacks() {
        return callbacks;
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
                Callback callback = callbackParser.parseMessage(msg).orElse(null);

                if (callback == null) {
                    state = State.FAILED;
                    break;
                }
                callbacks.add(callback);

                state = State.RECEIVED_CALLBACK;
                return callback;

            case RECEIVED_CALLBACK:
                CmdParser<R> responseParser = message.getCmd().getResponseParser();
                ApplicationCommandMsg<R> cmdMessage = ApplicationCommandMsg.parse(msg, responseParser).orElse(null);

                if (cmdMessage == null) {
                    callback = callbackParser.parseMessage(msg).orElse(null);

                    if (callback == null) {
                        state = State.FAILED;
                        break;
                    }

                    callbacks.add(callback);
                    return callback;
                }

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
