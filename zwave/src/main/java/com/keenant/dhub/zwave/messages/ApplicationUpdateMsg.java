package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.ApplicationUpdateEvent;
import com.keenant.dhub.zwave.exception.DataFrameException;
import com.keenant.dhub.zwave.exception.IllegalDataFrameTypeException;
import com.keenant.dhub.zwave.frame.DataFrameType;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ToString
public class ApplicationUpdateMsg implements InboundMessage {
    private static final byte ID = (byte) 0x49;

    private final State status;
    private final int nodeId;
    private final List<CmdClass> cmdClasses;

    public enum State {
        NODE_INFO_FAILED((byte) 0x81),
        NODE_INFO_RECEIVED((byte) 0x84);

        private final byte value;

        State(byte value) {
            this.value = value;
        }

        public static Optional<State> valueOf(byte value) {
            for (State status : values()) {
                if (status.value == value) {
                    return Optional.of(status);
                }
            }

            return Optional.empty();
        }
    }

    public static Optional<ApplicationUpdateMsg> parse(UnknownMessage msg) throws DataFrameException {
        ByteList data = msg.getDataBytes();
        DataFrameType type = msg.getType();

        if (ID != data.get(0)) {
            return Optional.empty();
        }

        if (type != DataFrameType.REQ) {
            throw new IllegalDataFrameTypeException(type);
        }

        State state = State.valueOf(data.get(1)).orElse(null);

        if (state == null) {
            throw new DataFrameException("Invalid application update state.");
        }

        if (state == State.NODE_INFO_FAILED) {
            // Todo: Failed
            return Optional.empty();
        }

        int nodeId = data.get(2) & 0xFF;
        int length = data.get(3);

        // Todo
        byte basic = data.get(4);
        byte generic = data.get(5);
        byte specific = data.get(6);

        ByteList cmdClassBytes = data.subList(7, 7 + length - 3);
        List<Integer> cmdClassInts = cmdClassBytes.stream()
                .map((bite) -> bite & 0xFF)
                .collect(Collectors.toList());

        List<CmdClass> cmdClasses = CmdClass.getCmdClasses(cmdClassInts);

        return Optional.of(new ApplicationUpdateMsg(state, nodeId, cmdClasses));
    }

    private ApplicationUpdateMsg(State status, int nodeId, List<CmdClass> cmdClasses) {
        this.status = status;
        this.nodeId = nodeId;
        this.cmdClasses = cmdClasses;
    }

    public State getStatus() {
        return status;
    }

    public int getNodeId() {
        return nodeId;
    }

    public List<CmdClass> getCmdClasses() {
        return cmdClasses;
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public InboundMessageEvent createEvent(Controller controller) {
        return new ApplicationUpdateEvent(controller, this);
    }
}
