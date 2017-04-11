package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.Message;
import com.keenant.dhub.zwave.UnknownMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.NodeListReplyEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.NodeListMsg.Reply;
import com.keenant.dhub.zwave.transaction.ReplyTransaction;
import lombok.ToString;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ToString
public class NodeListMsg implements Message<ReplyTransaction<Reply>> {
    private static final byte ID = (byte) 0x02;
    private static final int NODE_BITMASK_SIZE = 29;

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

        byte version = data.get(1);
        byte capabilities = data.get(2);

        Set<Integer> nodeIds = new HashSet<>();
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
        nodeIds = Collections.unmodifiableSet(nodeIds);

        byte chipType = data.get(data.size() - 2);
        byte chipVersion = data.get(data.size() - 1);

        return Optional.of(new Reply(version, capabilities, nodeIds, chipType, chipVersion));
    }

    @ToString
    public static class Reply implements InboundMessage {
        private final byte version;
        private final byte capabilities;
        private final Set<Integer> nodeIds;
        private final byte chipType;
        private final byte chipVersion;

        public Reply(byte version, byte capabilities, Set<Integer> nodeIds, byte chipType, byte chipVersion) {
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
            return new NodeListReplyEvent(controller, this);
        }

        public byte getVersion() {
            return version;
        }

        public byte getCapabilities() {
            return capabilities;
        }

        public Set<Integer> getNodeIds() {
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
