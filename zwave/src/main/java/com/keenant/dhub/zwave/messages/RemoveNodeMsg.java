package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.RemoveNodeCallbackEvent;
import com.keenant.dhub.zwave.exception.DataFrameException;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.transaction.RemoveNodeTxn;
import lombok.ToString;

import java.util.Optional;

@ToString
public class RemoveNodeMsg implements Message<RemoveNodeTxn> {
    private static final byte ID = 0x4B;

    private static final byte MODE_ANY = 0x01;
    private static final byte MODE_STOP = 0x05;

    private static final byte STATUS_LEARN_READY = 0x01;
    private static final byte STATUS_NODE_FOUND = 0x02;
    private static final byte STATUS_REMOVING_SLAVE = 0x03;
    private static final byte STATUS_REMOVING_CONTROLLER = 0x04;
    private static final byte STATUS_DONE = 0x06;
    private static final byte STATUS_FAILED = 0x07;


    public enum Mode {
        ANY,
        STOP
    }

    public enum State {
        LEARN_READY,
        NODE_FOUND,
        REMOVING_SLAVE,
        REMOVING_CONTROLLER,
        DONE,
        FAILED
    }

    private final Mode mode;

    public RemoveNodeMsg(Mode mode) {
        this.mode = mode;
    }

    public RemoveNodeMsg() {
        this(Mode.ANY);
    }

    @Override
    public ByteList toDataBytes() {
        return new ByteList(ID, mode == Mode.ANY ? MODE_ANY : MODE_STOP);
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public RemoveNodeTxn createTransaction(Controller controller) {
        if (mode == Mode.STOP) {
            throw new IllegalStateException("Cannot start stop node removal transaction.");
        }
        return new RemoveNodeTxn(controller, this, RemoveNodeMsg::parseCallback);
    }

    private static Optional<Callback> parseCallback(UnknownMessage msg) {
        ByteList data = msg.getDataBytes();
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        byte callbackId = data.get(1);
        byte statusByte = data.get(2);
        State status;

        switch (statusByte) {
            case STATUS_LEARN_READY:
                status = State.LEARN_READY;
                break;
            case STATUS_NODE_FOUND:
                status = State.NODE_FOUND;
                break;
            case STATUS_REMOVING_SLAVE:
                status = State.REMOVING_SLAVE;
                break;
            case STATUS_REMOVING_CONTROLLER:
                status = State.REMOVING_CONTROLLER;
                break;
            case STATUS_FAILED:
                status = State.FAILED;
                break;
            case STATUS_DONE:
                status = State.DONE;
                break;
            default:
                throw new DataFrameException("Invalid remove node status.");
        }

        Integer nodeId = data.get(3) == 0x00 ? null : data.get(3) & 0xFF;
        byte nodeLen = data.get(4);
        if (nodeLen > 0) {
            // Todo...
        }

        return Optional.of(new Callback(callbackId, status, nodeId));
    }

    @ToString
    public static class Callback implements InboundMessage {
        private final byte callbackId;
        private final State state;
        private final Integer nodeId;

        private Callback(byte callbackId, State state, Integer nodeId) {
            this.callbackId = callbackId;
            this.state = state;
            this.nodeId = nodeId;
        }

        public byte getCallbackId() {
            return callbackId;
        }

        public State getState() {
            return state;
        }

        public Optional<Integer> getNodeId() {
            return Optional.ofNullable(nodeId);
        }

        @Override
        public DataFrameType getType() {
            return DataFrameType.REQ;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new RemoveNodeCallbackEvent(controller, this);
        }
    }
}
