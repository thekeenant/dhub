package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import com.keenant.dhub.zwave.ResponsiveMessage;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.frame.UnknownDataFrame;
import lombok.ToString;

import java.util.Optional;

@ToString
public class ReqResTransaction<Res extends IncomingMessage> extends Transaction {
    private final ResponsiveMessage<ReqResTransaction<Res>, Res> message;
    private State state;
    private Res response;

    enum State {
        SENT,
        WAITING,
        DONE,
        FAILED
    }

    public ReqResTransaction(Controller controller, ResponsiveMessage<ReqResTransaction<Res>, Res> message, Priority priority) {
        super(controller, priority);
        this.message = message;
        this.state = null;
        this.response = null;
    }

    public Optional<Res> getResponse() {
        return Optional.ofNullable(response);
    }

    @Override
    public void start() {
        queue(message);
        state = State.SENT;
    }

    @Override
    public boolean isFinished() {
        if (!getOutgoing().isEmpty()) {
            return false;
        }

        return state == State.DONE || state == State.FAILED;
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
    public IncomingMessage handle(UnknownDataFrame frame) {
        switch (state) {
            case WAITING:
                ByteList data = frame.toDataBytes();
                Res res = message.parseResponse(data).orElse(null);
                if (res == null) {
                    state = State.FAILED;
                    break;
                }
                else {
                    getController().onReceive(res);
                }

                response = res;
                queue(Status.ACK);
                state = State.DONE;
                return res;
            default:
                queue(Status.CAN);
                state = State.FAILED;
                break;
        }

        return frame;
    }
}
