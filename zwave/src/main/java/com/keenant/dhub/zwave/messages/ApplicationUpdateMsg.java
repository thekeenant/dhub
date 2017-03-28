package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.ApplicationUpdateEvent;
import com.keenant.dhub.zwave.exception.DataFrameException;
import com.keenant.dhub.zwave.exception.IllegalDataFrameTypeException;
import com.keenant.dhub.zwave.frame.DataFrameType;
import lombok.ToString;

import java.util.Optional;

@ToString
public class ApplicationUpdateMsg implements InboundMessage {
    private static final byte ID = (byte) 0x49;

    private final Status status;
    private final int nodeId;

    public enum Status {
        NODE_INFO_RECEIVED((byte) 0x84);

        private byte value;

        Status(byte value) {
            this.value = value;
        }

        public static Optional<Status> valueOf(byte value) {
            for (Status status : values()) {
                if (status.value == value) {
                    return Optional.of(status);
                }
            }

            return Optional.empty();
        }
    }

    private ApplicationUpdateMsg(Status status, int nodeId) {
        this.status = status;
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public InboundMessageEvent createEvent(Controller controller) {
        return new ApplicationUpdateEvent(controller, this);
    }

    public static Optional<ApplicationUpdateMsg> parse(ByteList data, DataFrameType type) throws DataFrameException {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        if (type != DataFrameType.REQ) {
            throw new IllegalDataFrameTypeException(type);
        }

        try {
            Status status = Status.valueOf(data.get(1)).orElse(null);

            if (status == null) {
                // Todo: What should we do here?
                return Optional.empty();
            }

            byte nodeId = data.get(2);
            int length = data.get(3);

            // Todo
            ByteList nodeData = data.subList(4, 4 + length);

            return Optional.of(new ApplicationUpdateMsg(status, nodeId));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
