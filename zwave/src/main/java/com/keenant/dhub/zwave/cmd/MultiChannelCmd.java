package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.EndPointReport;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelEndPointReportEvent;
import com.keenant.dhub.zwave.exception.CommandFrameException;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.messages.ApplicationUpdateMsg;
import lombok.ToString;

import java.util.Optional;

/**
 * Multi channel command class (v4).
 */
public class MultiChannelCmd implements CmdClass<EndPointReport> {
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
    public Encap encap(int endPoint, Cmd cmd) {
        return new Encap(endPoint, cmd);
    }

    @Override
    public EndPointReport parseInboundCmd(ByteList data) throws CommandFrameException {
        if (data.get(0) == ID_END_POINT_REPORT) {
            boolean identical = (data.get(1) & 0x40) > 0;
            int count = data.get(2) & 0x3F;
            return new EndPointReport(identical, count);
        }

        throw new CommandFrameException();
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
            return Optional.of(INSTANCE);
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
    public static class Encap implements Cmd {
        private final int endPoint;
        private final Cmd cmd;

        private Encap(int endPoint, Cmd cmd) {
            this.endPoint = endPoint;
            this.cmd = cmd;
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
        public Optional<MessageParser> getResponseParser() {
            return Optional.empty();
        }
    }
}
