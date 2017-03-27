package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import com.keenant.dhub.zwave.transaction.Transaction;
import lombok.ToString;

import java.util.Optional;

@ToString
public class MemoryGetIdMsg implements ResponsiveMessage<ReqResTransaction<Response>, Response> {
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
    public ReqResTransaction<Response> createTransaction(Priority priority) {
        return new ReqResTransaction<>(this, priority);
    }

    @Override
    public Optional<Response> parseResponse(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        return Optional.of(new Response(data));
    }

    public static class Response extends IncomingDataFrame {
        public Response(ByteList data) {
            super(data);
        }
    }
}
