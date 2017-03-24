package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Byteable;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;
import lombok.ToString;

import java.util.Optional;

@ToString
public class SendDataMsg implements Message<Response> {
    public static final byte TRANSMIT_OPTION_ACK = 0x01;
    public static final byte TRANSMIT_OPTION_AUTO_ROUTE = 0x04;
    public static final byte TRANSMIT_OPTION_EXPLORE = (byte) 0x20;
    public static final byte TRANSMIT_OPTIONS_ALL = TRANSMIT_OPTION_ACK | TRANSMIT_OPTION_AUTO_ROUTE | TRANSMIT_OPTION_EXPLORE;

    private static final byte ID = (byte) 0x13;
    private static byte nextCallbackId = 0x01;

    private final byte nodeId;
    private final Byteable data;
    private final byte callbackId;
    private final byte transmitOptions;

    public SendDataMsg(byte nodeId, Byteable data, byte transmitOptions) {
        this.nodeId = nodeId;
        this.data = data;
        this.callbackId = nextCallbackId;
        nextCallbackId += (byte) 0x01;
        this.transmitOptions = transmitOptions;
    }

    public SendDataMsg(byte nodeId, Byteable data) {
        this(nodeId, data, (byte) 0);
    }

    public SendDataMsg(int nodeId, Byteable data, byte transmitOptions) {
        this((byte) nodeId, data, transmitOptions);
    }

    public SendDataMsg(int nodeId, Byteable data) {
        this(nodeId, data, (byte) 0);
    }

    @Override
    public ByteList toDataBytes() {
        ByteList data = this.data.toBytes();

        ByteList bites = new ByteList();
        bites.add(ID);
        bites.add(nodeId);
        bites.add((byte) data.size());
        bites.addAll(data);
        bites.add(nextCallbackId);
        bites.add(transmitOptions);

        return bites;
    }

    @Override
    public DataFrameType getType() {
        return DataFrameType.REQ;
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
            return Optional.of(new Response(data, value, funcId, txStatus));
        }

        return Optional.of(new Response(data, value));
    }

    @ToString
    public static class Response extends IncomingDataFrame {
        private final boolean value;
        private final Byte funcId;
        private final Byte txStatus;

        private Response(ByteList data, boolean value) {
            super(data);
            this.value = value;
            this.funcId = null;
            this.txStatus = null;
        }

        private Response(ByteList data, boolean value, byte funcId, byte txStatus) {
            super(data);
            this.value = value;
            this.funcId = funcId;
            this.txStatus = txStatus;
        }
    }
}