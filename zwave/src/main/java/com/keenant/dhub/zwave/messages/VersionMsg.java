package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.VersionReplyEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.VersionMsg.Reply;
import com.keenant.dhub.zwave.transaction.ReplyTransaction;
import lombok.ToString;

import java.util.Optional;

@ToString
public class VersionMsg implements Message<ReplyTransaction<Reply>> {
    private static final byte ID = (byte) 0x15;

    @Override
    public ByteList toDataBytes() {
        return new ByteList(ID);
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public ReplyTransaction<Reply> createTransaction(Controller controller) {
        return new ReplyTransaction<>(controller, this, this::parseReply);
    }

    private Optional<Reply> parseReply(UnknownMessage msg) {
        ByteList data = msg.getDataBytes();
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        return Optional.of(new Reply());
    }

    public static class Reply implements InboundMessage {
        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new VersionReplyEvent(controller, this);
        }
    }
}
