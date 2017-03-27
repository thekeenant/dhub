package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import lombok.ToString;

import java.util.Optional;

/**
 * The basic command class.
 */
@ToString
public class BasicCmd {
    private static final byte ID = (byte) 0x20;
    private static final byte SET = (byte) 0x01;
    private static final byte GET = (byte) 0x02;
    private static final byte REPORT = (byte) 0x03;

    private static final Get GET_INSTANCE = new Get();

    /**
     * Create a new basic set command.
     * @param value The basic value, between 0 and 255.
     * @return The new set command.
     * @throws IllegalArgumentException If the value is out of range.
     */
    public static Set set(int value) throws IllegalArgumentException {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Value must be between 0 and 255.");
        }
        return new Set(value);
    }

    /**
     * @return The basic get command.
     */
    public static Get get() {
        return GET_INSTANCE;
    }

    /**
     * Attempt to parse an incoming basic report command.
     * @param data The raw data.
     * @return The report command, empty if the data didn't match.
     */
    public static Optional<Report> parseReport(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        byte type = data.get(1);

        if (type == REPORT) {
            int value = data.get(2) & 0xFF;
            return Optional.of(new Report(value));
        }

        return Optional.empty();
    }

    @ToString
    public static class Set implements Cmd {
        private final int value;

        private Set(int value) {
            this.value = value;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, SET, value);
        }
    }

    @ToString
    public static class Get implements Cmd {
        private Get() {

        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, GET);
        }
    }

    @ToString
    public static class Report implements IncomingCmd {
        private final int value;

        private Report(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, REPORT);
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new BasicReportEvent(controller, nodeId, this);
        }
    }
}
