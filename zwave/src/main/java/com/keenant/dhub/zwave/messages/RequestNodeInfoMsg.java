package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.RequestNodeInfoReplyEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg.Reply;
import com.keenant.dhub.zwave.transaction.ReplyCallbackTransaction;
import lombok.ToString;

import java.util.Optional;

@ToString
public class RequestNodeInfoMsg implements Message<ReplyCallbackTransaction<Reply, ApplicationUpdateMsg>> {
    private static final byte ID = (byte) 0x60;

    private final int nodeId;

    public static RequestNodeInfoMsg get(int nodeId) {
        return new RequestNodeInfoMsg(nodeId);
    }

    private RequestNodeInfoMsg(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public ByteList toDataBytes() {
        return new ByteList(ID, nodeId);
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public ReplyCallbackTransaction<Reply, ApplicationUpdateMsg> createTransaction(Controller controller) {
        return new ReplyCallbackTransaction<>(controller, this, this::parseReply, ApplicationUpdateMsg::parse);
    }

    private Optional<Reply> parseReply(UnknownMessage msg) {
        ByteList data = msg.getDataBytes();

        if (ID != data.get(0)) {
            return Optional.empty();
        }

        boolean value = data.get(1) != (byte) 0x00;

        return Optional.of(new Reply(value));
    }

    @ToString
    public static class Reply implements InboundMessage {
        private final boolean value;

        public Reply(boolean value) {
            this.value = value;
        }

        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new RequestNodeInfoReplyEvent(controller, this);
        }

        public boolean getValue() {
            return value;
        }
    }
}
