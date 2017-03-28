package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.SwitchBinaryReportEvent;
import lombok.ToString;

import java.util.Optional;

/**
 * The binary switch command class.
 */
@ToString
public class SwitchBinaryCmd {
    private static final byte ID = (byte) 0x25;
    private static final byte ID_SET = (byte) 0x01;
    private static final byte ID_GET = (byte) 0x02;
    private static final byte ID_REPORT = (byte) 0x03;

    private static final Set SET_ON = new Set(true);
    private static final Set SET_OFF = new Set(false);
    private static final Get GET = new Get();
    private static final Report REPORT_ON = new Report(true);
    private static final Report REPORT_OFF = new Report(false);

    /**
     * Get the binary switch set command.
     * @param value True for on, false for off.
     * @return The new command.
     */
    public static Set set(boolean value) {
        return value ? SET_ON : SET_OFF;
    }

    /**
     * @return Binary switch on command
     */
    public static Set setOn() {
        return SET_ON;
    }

    /**
     * @return Binary switch off command
     */
    public static Set setOff() {
        return SET_OFF;
    }

    /**
     * @return The get command.
     */
    public static Get get() {
        return GET;
    }

    /**
     * Attempt to parse an inbound switch binary report command.
     * @param data The raw data.
     * @return The report command, empty if the data didn't match.
     */
    public static Optional<Report> parseReport(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        byte type = data.get(1);

        if (type == ID_REPORT) {
            boolean value = data.get(2) == (byte) 0x01;
            Report report = value ? REPORT_ON : REPORT_OFF;
            return Optional.of(report);
        }

        return Optional.empty();
    }

    @ToString
    public static class Set implements Cmd {
        private final boolean value;

        private Set(boolean value) {
            this.value = value;
        }

        @Override
        public ByteList toBytes() {
            // Todo: Final byte is duration
            return new ByteList(ID, ID_SET, value ? (byte) 0xFF : (byte) 0x00, 0x00);
        }
    }

    @ToString
    public static class Get implements Cmd {
        private Get() {

        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, ID_GET);
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
