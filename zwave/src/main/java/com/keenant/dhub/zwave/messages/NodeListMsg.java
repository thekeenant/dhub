package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.ResponsiveMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.NodeListEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.NodeListMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NodeListMsg implements ResponsiveMessage<ReqResTransaction<Response>, Response> {
    public static final byte ID = (byte) 0x04;

    private static final NodeListMsg INSTANCE = new NodeListMsg();

    public static NodeListMsg get() {
        return INSTANCE;
    }

    private NodeListMsg() {

    }

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

        byte version = data.get(1);
        byte capabilities = data.get(2);
        byte nodeCount = data.get(3);
        List<Integer> nodeIds = new ArrayList<>();
        for (int i = 4; i < 4 + nodeCount; i++) {
            nodeIds.add((int) data.get(i));
        }
        byte chipType = data.get(3 + nodeCount + 1);
        byte chipVersion = data.get(4 + nodeCount + 1);

        // Todo...

        return Optional.empty();
    }

    public static class Response implements InboundMessage {

        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new NodeListEvent(controller, this);
        }
    }
}
