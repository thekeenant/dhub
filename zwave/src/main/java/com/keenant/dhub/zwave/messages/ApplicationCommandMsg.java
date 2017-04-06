package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.ApplicationCommandEvent;
import com.keenant.dhub.zwave.exception.DataFrameException;
import com.keenant.dhub.zwave.exception.IllegalDataFrameTypeException;
import com.keenant.dhub.zwave.frame.DataFrameType;
import lombok.ToString;

import java.util.Optional;

@ToString
public class ApplicationCommandMsg<C extends InboundCmd> implements InboundMessage {
    private static final byte ID = (byte) 0x04;

    private final byte status;
    private final int nodeId;
    private final C cmd;

    @SuppressWarnings("unchecked")
    public static <T extends InboundCmd> Optional<ApplicationCommandMsg<T>> parse(UnknownMessage msg, CmdParser<T> parser) throws DataFrameException {
        ByteList data = msg.getDataBytes();
        DataFrameType type = msg.getType();

        if (ID != data.get(0)) {
            return Optional.empty();
        }

        if (type != DataFrameType.REQ) {
            throw new IllegalDataFrameTypeException(type);
        }

        try {
            byte status = data.get(1);
            byte nodeId = data.get(2);
            int length = data.get(3);

            ByteList cmdData = data.subList(4, 4 + length);

            T cmd;

            if (parser == null) {
                cmd = (T) InboundCmd.parse(cmdData).orElse(null);
            }
            else {
                cmdData = cmdData.subList(1, cmdData.size());
                cmd = parser.parseInboundCmd(cmdData);
            }

            if (cmd == null) {
                return Optional.empty();
            }

            return Optional.of(new ApplicationCommandMsg<>(status, nodeId, cmd));
        } catch (Exception e) {
            throw new DataFrameException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends InboundCmd> Optional<ApplicationCommandMsg<T>> parse(UnknownMessage msg) throws DataFrameException {
        return parse(msg, null);
    }

    private ApplicationCommandMsg(byte status, int nodeId, C cmd) {
        this.status = status;
        this.nodeId = nodeId;
        this.cmd = cmd;
    }

    public int getNodeId() {
        return nodeId;
    }

    public C getCmd() {
        return cmd;
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public InboundMessageEvent createEvent(Controller controller) {
        return new ApplicationCommandEvent(controller, this);
    }
}
