package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelCapabilityReportEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelEndPointReportEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelInboundEncapEvent;
import com.keenant.dhub.zwave.exception.CommandFrameException;
import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.util.EndPoint;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi channel command class (v4).
 */
@ToString
public class MultiChannelCmd implements CmdClass<InboundCmd> {
    public static final MultiChannelCmd INSTANCE = new MultiChannelCmd();

    private static final byte ID = (byte) 0x60;

    private static final byte ID_END_POINT_GET = (byte) 0x07;
    private static final byte ID_END_POINT_REPORT = (byte) 0x08;
    private static final byte ID_CAPABILITY_GET = (byte) 0x09;
    private static final byte ID_CAPABILITY_REPORT = (byte) 0x0A;
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
     * @param endPoint The endpoint to retrieve capabilities for.
     * @return The multi channel capability get command.
     */
    public CapabilityGet capabilityGet(int endPoint) {
        return new CapabilityGet(endPoint);
    }

    /**
     * Create a new multi endpoint encapsulation of a command that is responsive.
     * @param endPoint The mc/destination id (subnode).
     * @param cmd The command to send to the channel.
     * @return The created multi mc encapsulation.
     */
    public <C extends ResponsiveCmd<R>, R extends InboundCmd> ResponsiveEncap<C, R> encap(int endPoint, C cmd) {
        return new ResponsiveEncap<>(endPoint, cmd);
    }

    /**
     * Create a new multi endpoint encapsulation of a command.
     * @param endPoint The mc/destination id (subnode).
     * @param cmd The command to send to the channel.
     * @return The created multi mc encapsulation.
     */
    @SuppressWarnings("unchecked")
    public <C extends Cmd> Encap<C> encap(int endPoint, C cmd) {
        // Todo: Check this
        if (cmd instanceof ResponsiveCmd) {
            ResponsiveCmd responsive = (ResponsiveCmd) cmd;
            return (Encap<C>) encap(endPoint, responsive);
        }
        return new Encap<>(endPoint, cmd);
    }

    public InboundCmd parseInboundCmd(ByteList data) throws CommandFrameException {
        if (data.get(0) == ID_END_POINT_REPORT) {
            return parseEndPointReport(data);
        }
        if (data.get(0) == ID_CAPABILITY_REPORT) {
            return parseCapabilityReport(data);
        }

        throw new CommandFrameException();
    }

    private static EndPointReport parseEndPointReport(ByteList data) {
        boolean identical = (data.get(1) & 0x40) > 0;
        int count = data.get(2) & 0x3F;
        return new EndPointReport(identical, count);
    }

    private static CapabilityReport parseCapabilityReport(ByteList data) {
        if (data.get(0) != ID_CAPABILITY_REPORT) {
            throw new CommandFrameException();
        }

        int endPoint = data.get(1);

        // Todo
        byte basic = data.get(2);
        byte generic = data.get(3);
        byte specific = data.get(4);

        List<CmdClass> cmdClasses = new ArrayList<>();
        for (int i = 5; i < data.size(); i++) {
            CmdClass.getCmdClass(data.get(i & 0xFF)).ifPresent(cmdClasses::add);
        }

        return new CapabilityReport(endPoint, cmdClasses);
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
    public static class EndPointGet implements ResponsiveCmd<EndPointReport> {
        private EndPointGet() {

        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, ID_END_POINT_GET);
        }

        @Override
        public CmdParser<EndPointReport> getResponseParser() {
            return MultiChannelCmd::parseEndPointReport;
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
        public CmdEvent createEvent(Controller controller, EndPoint endPoint) {
            return new MultiChannelEndPointReportEvent(controller, endPoint, this);
        }
    }

    @ToString
    public static class CapabilityGet implements ResponsiveCmd<CapabilityReport> {
        private final int endPoint;

        public CapabilityGet(int endPoint) {
            this.endPoint = endPoint;
        }

        @Override
        public CmdParser<CapabilityReport> getResponseParser() {
            return MultiChannelCmd::parseCapabilityReport;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, ID_CAPABILITY_GET, endPoint);
        }
    }

    @ToString
    public static class CapabilityReport implements InboundCmd {
        private final int endPoint;
        private final List<CmdClass> cmdClasses;

        public CapabilityReport(int endPoint, List<CmdClass> cmdClasses) {
            this.endPoint = endPoint;
            this.cmdClasses = cmdClasses;
        }

        @Override
        public CmdEvent createEvent(Controller controller, EndPoint endPoint) {
            return new MultiChannelCapabilityReportEvent(controller, endPoint, this);
        }

        public int getEndPoint() {
            return endPoint;
        }

        public List<CmdClass> getCmdClasses() {
            return cmdClasses;
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
        public CmdEvent createEvent(Controller controller, EndPoint endPoint) {
            return new MultiChannelInboundEncapEvent(controller, endPoint, this);
        }
    }

    public static class Encap<C extends Cmd> implements Cmd {
        private final int endPoint;
        private final C cmd;

        private Encap(int endPoint, C cmd) {
            this.endPoint = endPoint;
            this.cmd = cmd;
        }

        public int getEndPoint() {
            return endPoint;
        }

        public C getCmd() {
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
    }

    @ToString
    public static class ResponsiveEncap<C extends ResponsiveCmd<R>, R extends InboundCmd> extends Encap<C> implements ResponsiveCmd<InboundEncap<R>> {
        private ResponsiveEncap(int endPoint, C cmd) {
            super(endPoint, cmd);
        }

        @Override
        public CmdParser<InboundEncap<R>> getResponseParser() {
            CmdParser<R> parser = getCmd().getResponseParser();
            return data -> parseInboundEncap(data, parser);
        }
    }
}
