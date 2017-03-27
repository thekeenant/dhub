package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.cmd.Cmd;
import com.keenant.dhub.zwave.event.message.ApplicationCommandEvent;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import lombok.ToString;

import java.util.Optional;

@ToString
public class ApplicationCommandMsg implements IncomingMessage {
    private static final byte ID = (byte) 0x04;

    private final byte status;
    private final int nodeId;
    private final Cmd command;

    public ApplicationCommandMsg(byte status, int nodeId, Cmd command) {
        this.status = status;
        this.nodeId = nodeId;
        this.command = command;
    }

    public int getNodeId() {
        return nodeId;
    }

    public Optional<Cmd> getCommand() {
        return Optional.ofNullable(command);
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public IncomingMessageEvent createEvent(ZController controller) {
        return new ApplicationCommandEvent(controller, this);
    }

    public static Optional<ApplicationCommandMsg> parse(ByteList data, DataFrameType type) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        if (type != DataFrameType.REQ) {
            throw new IllegalArgumentException("Expected REQ frame type.");
        }

        byte status = data.get(1);
        byte nodeId = data.get(2);
        int length = data.get(3);

        ByteList cmdData = data.subList(4, 4 + length);
        Cmd cmd = Cmd.parse(cmdData).orElse(null);

        return Optional.of(new ApplicationCommandMsg(status, nodeId, cmd));
    }
}
