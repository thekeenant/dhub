package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.SendDataCallbackEvent;
import com.keenant.dhub.zwave.event.message.SendDataReplyEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.transaction.SendDataTransaction;
import com.keenant.dhub.zwave.util.ByteBuilder;
import lombok.ToString;

import java.util.Optional;

@ToString
public class SendDataMsg<C extends Cmd<R>, R extends InboundCmd> implements Message<SendDataTransaction<C, R>> {
    private static final byte ID = (byte) 0x13;

    private static final byte ID_TX_ACK = 0x01;
    private static final byte ID_TX_AUTO_ROUTE = 0x04;
    private static final byte ID_TX_EXPLORE = (byte) 0x20;

    private static final TxOptions TX_ALL = new TxOptions().all();
    private static byte nextCallbackId = 0x01;

    private final int nodeId;
    private final C cmd;
    private final byte callbackId;
    private final TxOptions txOptions;

    public SendDataMsg(int nodeId, C cmd) {
        this(nodeId, cmd, TX_ALL);
    }

    public SendDataMsg(int nodeId, C cmd, TxOptions txOptions) {
        this.nodeId = nodeId;
        this.cmd = cmd;
        this.callbackId = nextCallbackId;
        nextCallbackId += (byte) 0x01;
        this.txOptions = txOptions;
    }

    public C getCmd() {
        return cmd;
    }

    @Override
    public ByteList toDataBytes() {
        ByteList data = this.cmd.toBytes();

        ByteList bites = new ByteList();
        bites.add(ID);
        bites.add((byte) nodeId);
        bites.add((byte) data.size());
        bites.addAll(data);
        bites.add(callbackId);
        bites.add(txOptions.get());

        return bites;
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
    }

    @Override
    public SendDataTransaction<C, R> createTransaction(Controller controller) {
        return new SendDataTransaction<>(controller, this, this::parseReply, this::parseCallback);
    }

    private Optional<Reply> parseReply(UnknownMessage msg) {
        System.out.println("Reply: " + msg);
        ByteList data = msg.getDataBytes();

        if (data.get(0) != ID) {
            return Optional.empty();
        }

        boolean value = data.get(1) == 0x01;

        if (data.size() > 3) {
            // Todo: We can check funcId
            byte funcId = data.get(2);
            byte txStatus = data.get(3);
            return Optional.of(new Reply(value, funcId, txStatus));
        }

        return Optional.of(new Reply(value));
    }

    private Optional<Callback> parseCallback(UnknownMessage msg) {
        System.out.println("Callback: " + msg);
        ByteList data = msg.getDataBytes();

        if (data.get(0) != ID) {
            return Optional.empty();
        }

        // Todo...

        return Optional.of(new Callback());
    }

    @ToString
    public static class Reply implements InboundMessage {
        private final boolean value;
        private final Byte funcId;
        private final Byte txStatus;

        private Reply(boolean value) {
            this.value = value;
            this.funcId = null;
            this.txStatus = null;
        }

        private Reply(boolean value, byte funcId, byte txStatus) {
            this.value = value;
            this.funcId = funcId;
            this.txStatus = txStatus;
        }

        @Override
        public DataFrameType getType() {
            return DataFrameType.RES;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new SendDataReplyEvent(controller, this);
        }
    }

    @ToString
    public static class Callback implements InboundMessage {
        @Override
        public DataFrameType getType() {
            return DataFrameType.REQ;
        }

        @Override
        public InboundMessageEvent createEvent(Controller controller) {
            return new SendDataCallbackEvent(controller, this);
        }
    }

    public static class TxOptions extends ByteBuilder {
        public TxOptions ack() {
            with(ID_TX_ACK);
            return this;
        }

        public TxOptions autoRoute() {
            with(ID_TX_AUTO_ROUTE);
            return this;
        }

        public TxOptions explore() {
            with(ID_TX_EXPLORE);
            return this;
        }

        public TxOptions all() {
            reset();
            ack();
            autoRoute();
            return explore();
        }
    }
}
