package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import com.keenant.dhub.zwave.transaction.Transaction;
import lombok.ToString;

import java.math.BigInteger;
import java.nio.ByteBuffer;
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
    public ReqResTransaction<Response> createTransaction(ZController controller, Priority priority) {
        return new ReqResTransaction<>(controller, this, priority);
    }

    @Override
    public Optional<Response> parseResponse(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        // 4 bytes to represent homeId
        byte[] homeIdBytes = data.subList(1, 5).toByteArray();
        long homeId = homeIdBytes[0] & 0xFF;
        homeId |= (homeIdBytes[1] & 0xFF) << 8;
        homeId |= (homeIdBytes[2] & 0xFF) << 16;
        homeId |= (homeIdBytes[3] & 0xFF) << 24;

        int nodeId = data.get(5) & 0xFF;

        return Optional.of(new Response(data, homeId, nodeId));
    }

    @ToString
    public static class Response extends IncomingDataFrame {
        private final long homeId;
        private final int nodeId;

        public Response(ByteList data, long homeId, int nodeId) {
            super(data);
            this.homeId = homeId;
            this.nodeId = nodeId;
        }

        public long getHomeId() {
            return homeId;
        }

        public int getNodeId() {
            return nodeId;
        }
    }
}
