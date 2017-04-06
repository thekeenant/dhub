package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.SwitchBinaryReportEvent;
import com.keenant.dhub.zwave.exception.CommandFrameException;
import lombok.ToString;

import java.util.Optional;

/**
 * The binary switch command class.
 */
@ToString
public class SwitchBinaryCmd implements CmdClass<Report> {
    public static final SwitchBinaryCmd INSTANCE = new SwitchBinaryCmd();

    private static final byte ID = (byte) 0x25;
    private static final byte ID_SET = (byte) 0x01;
    private static final byte ID_GET = (byte) 0x02;
    private static final byte ID_REPORT = (byte) 0x03;

    private static final Set SET_ON = new Set(true);
    private static final Set SET_OFF = new Set(false);
    private static final Get GET = new Get();
    private static final Report REPORT_ON = new Report(true);
    private static final Report REPORT_OFF = new Report(false);

    private SwitchBinaryCmd() {

    }

    /**
     * Get the binary switch set command.
     * @param value True for on, false for off.
     * @return The new command.
     */
    public Set set(boolean value) {
        return value ? SET_ON : SET_OFF;
    }

    /**
     * @return Binary switch on command
     */
    public Set setOn() {
        return SET_ON;
    }

    /**
     * @return Binary switch off command
     */
    public Set setOff() {
        return SET_OFF;
    }

    /**
     * @return The get command.
     */
    public Get get() {
        return GET;
    }

    @Override
    public Report parseInboundCmd(ByteList data) throws CommandFrameException {
        byte type = data.get(0);

        if (type == ID_REPORT) {
            boolean value = data.get(1) != (byte) 0x00;
            return value ? REPORT_ON : REPORT_OFF;
        }

        throw new CommandFrameException();
    }

    @Override
    public byte getId() {
        return ID;
    }

    @ToString
    public static class Set implements Cmd<InboundCmd> {
        private final boolean value;

        private Set(boolean value) {
            this.value = value;
        }

        @Override
        public ByteList toBytes() {
            // Todo: Final byte is duration
            return new ByteList(ID, ID_SET, value ? (byte) 0xFF : (byte) 0x00, 0x00);
        }

        @Override
        public Optional<CmdParser<InboundCmd>> getResponseParser() {
            return Optional.empty();
        }
    }

    @ToString
    public static class Get implements Cmd<Report> {
        private Get() {

        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, ID_GET);
        }

        @Override
        public Optional<CmdParser<Report>> getResponseParser() {
            return Optional.of(INSTANCE);
        }
    }

    @ToString
    public static class Report implements InboundCmd {
        private final boolean value;

        private Report(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new SwitchBinaryReportEvent(controller, nodeId, this);
        }
    }
}
