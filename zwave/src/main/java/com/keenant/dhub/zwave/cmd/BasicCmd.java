package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.*;
import com.keenant.dhub.zwave.cmd.BasicCmd.Report;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import com.keenant.dhub.zwave.exception.CommandFrameException;
import lombok.ToString;

import java.util.Optional;

/**
 * The basic command class.
 */
@ToString
public class BasicCmd implements CmdClass<Report> {
    public static final BasicCmd INSTANCE = new BasicCmd();

    private static final byte ID = (byte) 0x20;
    private static final byte ID_SET = (byte) 0x01;
    private static final byte ID_GET = (byte) 0x02;
    private static final byte ID_REPORT = (byte) 0x03;

    private static final int MIN_VALUE = 0x00; // 0
    private static final int MAX_VALUE = 0x63; // 99
    private static final int ON_VALUE = 0xFF;

    private static final Set SET_MIN = new Set(MIN_VALUE);
    private static final Set SET_MAX = new Set(MAX_VALUE);
    private static final Set SET_ON = new Set(ON_VALUE);
    private static final Get GET = new Get();

    private BasicCmd() {

    }

    /**
     * Create a new basic set command.
     * @param value The basic value, between 0 and 99, or 255.
     * @return The new set command.
     * @throws IllegalArgumentException If the value is out of range.
     */
    public Set set(int value) throws IllegalArgumentException {
        if (value == MIN_VALUE) {
            return SET_MIN;
        }
        else if (value == MAX_VALUE) {
            return SET_MAX;
        }
        else if (value == ON_VALUE) {
            return SET_ON;
        }
        else if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Value must be between 0 and 255.");
        }
        return new Set(value);
    }

    /**
     * Create a new basic set command.
     * @param percent A value in [0,1].
     * @return The set command.
     */
    public Set setPercent(double percent) {
        double bounded;
        if (percent > 1) {
            bounded = 1;
        }
        else if (percent < 0) {
            bounded = 0;
        }
        else {
            bounded = percent;
        }

        int value = (int) Math.floor((double) (MAX_VALUE - 1) * bounded);
        return set(value);
    }

    /**
     * Create a new basic set command.
     * @param percent A value in [0,1].
     * @return The set command.
     */
    public Set setPercent(float percent) {
        return setPercent((double) percent);
    }

    /**
     * Create a new basic command with value set to max.
     *
     * @return The set command.
     */
    public Set setMax() {
        return SET_MAX;
    }

    /**
     * Create a new basic "on" command (value = 255). This should set the device to the
     * last value it was set to above 0, if it is off.
     *
     * @return The set command.
     */
    public Set setOn() {
        return SET_ON;
    }

    /**
     * Create a new basic set command to 0.
     * @return The set command.
     */
    public Set setOff() {
        return SET_MIN;
    }

    /**
     * @return The basic get command.
     */
    public Get get() {
        return GET;
    }

    @Override
    public Report parseInboundCmd(ByteList data) throws CommandFrameException {
        byte type = data.get(0);

        if (type == ID_REPORT) {
            int value = data.get(1) & 0xFF;
            return new Report(value);
        }

        throw new CommandFrameException();
    }

    @Override
    public byte getId() {
        return ID;
    }

    @ToString
    public static class Set implements Cmd<InboundCmd> {
        private final int value;

        private Set(int value) {
            this.value = value;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, ID_SET, value);
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
        private final int value;

        private Report(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public double getPercent() {
            return (double) value / MAX_VALUE;
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new BasicReportEvent(controller, nodeId, this);
        }
    }
}
