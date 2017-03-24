package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.messages.Message;
import lombok.ToString;

import java.util.Optional;

@ToString
public class ReqResTransaction<Res extends IncomingDataFrame> extends Transaction {
    private final Message<Res> message;
    private State state;
    private Res response;

    enum State {
        SENT,
        WAITING,
        DONE,
        FAILED
    }

    public ReqResTransaction(Message<Res> message, Priority priority) {
        super(priority);
        this.message = message;
        this.state = null;
        this.response = null;
    }

    public ReqResTransaction(Message<Res> message) {
        this(message, Priority.MEDIUM);
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
    public IncomingDataFrame handle(IncomingDataFrame frame) {
        switch (state) {
            case WAITING:
                ByteList data = frame.toDataBytes();
                Res res = message.parseResponse(data).orElse(null);
                if (res == null) {
                    state = State.FAILED;
                    break;
                }

                response = res;
                queue(Status.ACK);
                state = State.DONE;
                return res;
            default:
                System.out.println(state);
                queue(Status.CAN);
                state = State.FAILED;
                break;
        }

        return frame;
    }
}
