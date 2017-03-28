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
import lombok.ToString;

import java.util.*;

@ToString
public class NodeListMsg implements ResponsiveMessage<ReqResTransaction<Response>, Response> {
    private static final byte ID = (byte) 0x02;

    private static final int NODE_BITMASK_SIZE = 29;

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

        List<Integer> nodeIds = new ArrayList<>();
        if (NODE_BITMASK_SIZE == data.get(3)) {
            for (int i = 0; i < NODE_BITMASK_SIZE; i++) {
                byte curr = data.get(i + 4);
                for (int j = 0; j < 8; j++) {
                    byte nodeId = (byte) ((i * 8) + j + 1);
                    if ((curr & (0x01 << j)) > 0) {
                        nodeIds.add((int) nodeId);
                    }
                }
            }
        }
        nodeIds = Collections.unmodifiableList(nodeIds);

        byte chipType = data.get(data.size() - 2);
        byte chipVersion = data.get(data.size() - 1);

        return Optional.of(new Response(version, capabilities, nodeIds, chipType, chipVersion));
    }

    @ToString
    public static class Response implements InboundMessage {
        private final byte version;
        private final byte capabilities;
        private final List<Integer> nodeIds;
        private final byte chipType;
        private final byte chipVersion;

        public Response(byte version, byte capabilities, List<Integer> nodeIds, byte chipType, byte chipVersion) {
            this.version = version;
            this.capabilities = capabilities;
            this.nodeIds = nodeIds;
            this.chipType = chipType;
            this.chipVersion = chipVersion;
        }

        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new NodeListEvent(controller, this);
        }

        public byte getVersion() {
            return version;
        }

        public byte getCapabilities() {
            return capabilities;
        }

        public List<Integer> getNodeIds() {
            return nodeIds;
        }

        public byte getChipType() {
            return chipType;
        }

        public byte getChipVersion() {
            return chipVersion;
        }
    }
}
