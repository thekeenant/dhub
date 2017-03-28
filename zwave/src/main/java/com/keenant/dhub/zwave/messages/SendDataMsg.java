package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Byteable;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundMessage;
import com.keenant.dhub.zwave.ResponsiveMessage;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.message.SendDataEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import com.keenant.dhub.zwave.util.ByteBuilder;
import lombok.ToString;

import java.util.Optional;

@ToString
public class SendDataMsg implements ResponsiveMessage<ReqResTransaction<Response>, Response> {
    private static final byte ID = (byte) 0x13;

    private static final byte ID_TX_ACK = 0x01;
    private static final byte ID_TX_AUTO_ROUTE = 0x04;
    private static final byte ID_TX_EXPLORE = (byte) 0x20;

    private static final TxOptions TX_ALL = new TxOptions().all();
    private static byte nextCallbackId = 0x01;

    private final int nodeId;
    private final Byteable data;
    private final byte callbackId;
    private final TxOptions txOptions;

    public static SendDataMsg get(int nodeId, Byteable data, TxOptions txOptions) {
        return new SendDataMsg(nodeId, data, txOptions);
    }

    public static SendDataMsg get(int nodeId, Byteable data) {
        return get(nodeId, data, TX_ALL);
    }

    private SendDataMsg(int nodeId, Byteable data, TxOptions txOptions) {
        this.nodeId = nodeId;
        this.data = data;
        this.callbackId = nextCallbackId;
        nextCallbackId += (byte) 0x01;
        this.txOptions = txOptions;
    }

    @Override
    public ByteList toDataBytes() {
        ByteList data = this.data.toBytes();

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
    public ReqResTransaction<Response> createTransaction(Controller controller) {
        return new ReqResTransaction<>(controller, this);
    }

    @Override
    public Optional<Response> parseResponse(ByteList data) {
        if (data.get(0) != ID) {
            return Optional.empty();
        }

        boolean value = data.get(1) == 0x01;

        if (data.size() > 3) {
            byte funcId = data.get(2);
            byte txStatus = data.get(3);
            return Optional.of(new Response(value, funcId, txStatus));
        }

        return Optional.of(new Response(value));
    }

    @ToString
    public static class Response implements InboundMessage {
        private final boolean value;
        private final Byte funcId;
        private final Byte txStatus;

        private Response(boolean value) {
            this.value = value;
            this.funcId = null;
            this.txStatus = null;
        }

        private Response(boolean value, byte funcId, byte txStatus) {
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
            return new SendDataEvent(controller, this);
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
