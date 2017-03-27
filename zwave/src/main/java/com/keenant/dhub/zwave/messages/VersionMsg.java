package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingMessage;
import com.keenant.dhub.zwave.ResponsiveMessage;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.event.message.VersionEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.VersionMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import lombok.ToString;

import java.util.Optional;

@ToString
public class VersionMsg implements ResponsiveMessage<ReqResTransaction<Response>, Response> {
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
    public ReqResTransaction<Response> createTransaction(Controller controller) {
        return new ReqResTransaction<>(controller, this);
    }

    @Override
    public Optional<Response> parseResponse(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        return Optional.of(new Response());
    }

    public static class Response implements IncomingMessage {
        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public IncomingMessageEvent createEvent(Controller controller) {
            return new VersionEvent(controller, this);
        }
    }
}