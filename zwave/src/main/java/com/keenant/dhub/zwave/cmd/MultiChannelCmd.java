package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelEndPointReportEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelInboundEncapEvent;
import com.keenant.dhub.zwave.exception.CommandFrameException;
import lombok.ToString;

import java.util.Optional;

/**
 * Multi channel command class (v4).
 */
@ToString
public class MultiChannelCmd implements CmdClass<InboundCmd> {
    public static final MultiChannelCmd INSTANCE = new MultiChannelCmd();

    private static final byte ID = (byte) 0x60;

    private static final byte ID_END_POINT_GET = (byte) 0x07;
    private static final byte ID_END_POINT_REPORT = (byte) 0x08;
    private static final byte ID_ENCAP = (byte) 0x0D;

    private static final EndPointGet END_POINT_GET = new EndPointGet();

    private MultiChannelCmd() {

    }

    /**
     * @return The multi channel endpoint get command.
     */
    public EndPointGet endPointGet() {
        return END_POINT_GET;
    }

    /**
     * Create a new multi endpoint encapsulation of a command.
     * @param endPoint The mc/destination id (subnode).
     * @param cmd The command to send to the channel.
     * @return The created multi mc encapsulation.
     */
    public <T extends InboundCmd> Encap<T> encap(int endPoint, Cmd<T> cmd) {
        return new Encap<>(endPoint, cmd);
    }

    public InboundCmd parseInboundCmd(ByteList data) throws CommandFrameException {
        if (data.get(0) == ID_END_POINT_REPORT) {
            return parseReport(data);
        }

        throw new CommandFrameException();
    }

    private static EndPointReport parseReport(ByteList data) {
        boolean identical = (data.get(1) & 0x40) > 0;
        int count = data.get(2) & 0x3F;
        return new EndPointReport(identical, count);
    }

    public static <T extends InboundCmd> InboundEncap<T> parseInboundEncap(ByteList data, CmdParser<T> parser) {
        int sourceEndPoint = data.get(1);
        int destEndPoint = data.get(2);
        // Not 3 -> end because 3 is the cmd id
        ByteList cmdData = data.subList(4, data.size());
        T cmd = parser.parseInboundCmd(cmdData);
        return new InboundEncap<>(sourceEndPoint, cmd);
    }

    @Override
    public byte getId() {
        return ID;
    }

    @ToString
    public static class EndPointGet implements Cmd<EndPointReport> {
        private EndPointGet() {

        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, ID_END_POINT_GET);
        }

        @Override
        public Optional<CmdParser<EndPointReport>> getResponseParser() {
            return Optional.of(MultiChannelCmd::parseReport);
        }
    }

    @ToString
    public static class EndPointReport implements InboundCmd {
        private final boolean endPointsIdentical;
        private final int endPointCount;

        private EndPointReport(boolean endPointsIdentical, int endPointCount) {
            this.endPointsIdentical = endPointsIdentical;
            this.endPointCount = endPointCount;
        }

        public boolean isEndPointsIdentical() {
            return endPointsIdentical;
        }

        public int getEndPointCount() {
            return endPointCount;
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new MultiChannelEndPointReportEvent(controller, nodeId, this);
        }
    }

    @ToString
    public static class InboundEncap<T extends InboundCmd> implements InboundCmd {
        private final int endPoint;
        private final T cmd;

        private InboundEncap(int endPoint, T cmd) {
            this.endPoint = endPoint;
            this.cmd = cmd;
        }

        public int getEndPoint() {
            return endPoint;
        }

        public T getCmd() {
            return cmd;
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new MultiChannelInboundEncapEvent(controller, nodeId, this);
        }
    }

    @ToString
    public static class Encap<R extends InboundCmd> implements Cmd<InboundEncap<R>> {
        private final int endPoint;
        private final Cmd<R> cmd;

        private Encap(int endPoint, Cmd<R> cmd) {
            this.endPoint = endPoint;
            this.cmd = cmd;
        }

        public int getEndPoint() {
            return endPoint;
        }

        public Cmd<R> getCmd() {
            return cmd;
        }

        @Override
        public ByteList toBytes() {
            // Todo: What
            byte sourceEndpoint = 0x00 & 0x7f;
            boolean bitAddr = false;

            byte destEndpoint = (byte) ((bitAddr ? (byte) 0x80 : (byte) 0x00) | (byte) endPoint);

            ByteList list = new ByteList(ID, ID_ENCAP, sourceEndpoint, destEndpoint);
            list.addAll(cmd.toBytes());
            return list;
        }

        @Override
        public Optional<CmdParser<InboundEncap<R>>> getResponseParser() {
            CmdParser<R> parser = cmd.getResponseParser().orElse(null);

            if (parser == null) {
                return Optional.empty();
            }

            return Optional.of(data -> parseInboundEncap(data, parser));
        }
    }
}
