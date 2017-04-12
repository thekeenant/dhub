package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.MemoryGetIdReplyEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg.Reply;
import com.keenant.dhub.zwave.transaction.ReplyTxn;
import lombok.ToString;

import java.util.Optional;

@ToString
public class MemoryGetIdMsg implements Message<ReplyTxn<Reply>> {
    private static final byte ID = (byte) 0x20;

    @Override
    public ByteList toDataBytes() {
        return new ByteList(ID);
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public ReplyTxn<Reply> createTransaction(Controller controller) {
        return new ReplyTxn<>(controller, this, this::parseReply);
    }

    private Optional<Reply> parseReply(UnknownMessage msg) {
        ByteList data = msg.getDataBytes();

        if (ID != data.get(0)) {
            return Optional.empty();
        }

        // Todo: Is this right?
        // 4 bytes to represent homeId
        byte[] homeIdBytes = data.subList(1, 5).toByteArray();
        long homeId = homeIdBytes[0] & 0xFF;
        homeId |= (homeIdBytes[1] & 0xFF) << 8;
        homeId |= (homeIdBytes[2] & 0xFF) << 16;
        homeId |= (homeIdBytes[3] & 0xFF) << 24;

        int nodeId = data.get(5) & 0xFF;

        return Optional.of(new Reply(homeId, nodeId));
    }

    @ToString
    public static class Reply implements InboundMessage {
        private final long homeId;
        private final int nodeId;

        public Reply(long homeId, int nodeId) {
            this.homeId = homeId;
            this.nodeId = nodeId;
        }

        public long getHomeId() {
            return homeId;
        }

        public int getNodeId() {
            return nodeId;
        }

        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new MemoryGetIdReplyEvent(controller, this);
        }
    }
}
