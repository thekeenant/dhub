package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Cast;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.SwitchMultilevelReport;
import lombok.ToString;

import java.util.Optional;

/**
 * The basic command class.
 */
@ToString
public class SwitchMultilevelCmd {
    private static final byte ID = (byte) 0x26;
    private static final byte ID_SET = (byte) 0x01;
    private static final byte ID_GET = (byte) 0x02;
    private static final byte ID_REPORT = (byte) 0x03;

    private static final int MIN_VALUE = 0x00; // 0
    private static final int MAX_VALUE = 0x63; // 99

    private static final Set SET_MIN = new Set(MIN_VALUE);
    private static final Set SET_MAX = new Set(MAX_VALUE);
    private static final Get GET = new Get();

    /**
     * Create a new basic set command.
     * @param value The basic value, between 0 and 99, or 255.
     * @return The new set command.
     * @throws IllegalArgumentException If the value is out of range.
     */
    public static Set set(int value) throws IllegalArgumentException {
        if (value == MIN_VALUE) {
            return SET_MIN;
        }
        else if (value == MAX_VALUE) {
            return SET_MAX;
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
    public static Set setPercent(double percent) {
        if (percent > 1) {
            percent = 1;
        }
        else if (percent < 0) {
            percent = 0;
        }

        int value = (int) Math.floor((double) (MAX_VALUE - 1) * percent);
        return set(value);
    }

    /**
     * Create a new basic set command.
     * @param percent A value in [0,1].
     * @return The set command.
     */
    public static Set setPercent(float percent) {
        return setPercent((double) percent);
    }

    /**
     * Create a new basic command with value set to max.
     *
     * @return The set command.
     */
    public static Set setMax() {
        return SET_MAX;
    }

    /**
     * Create a new basic set command to 0.
     * @return The set command.
     */
    public static Set setOff() {
        return SET_MIN;
    }

    /**
     * @return The basic get command.
     */
    public static Get get() {
        return GET;
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

        System.out.println(data);

        byte type = data.get(1);

        if (type == ID_REPORT) {
            int current = data.get(2);
            Integer target = data.getSafely(3).map(Cast::toInt).orElse(null);
            Integer duration = data.getSafely(4).map(Cast::toInt).orElse(null);
            return Optional.of(new Report(current, target, duration));
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
            return new ByteList(ID, ID_SET, value, 100);
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
    public static class Report implements IncomingCmd {
        private final int current;
        private final Integer target;
        private final Integer duration;

        private Report(int current, Integer target, Integer duration) {
            this.current = current;
            this.target = target;
            this.duration = duration;
        }

        /**
         * @return The reported multilevel value.
         */
        public int getCurrent() {
            return current;
        }

        /**
         * Get the target level.
         * @return The target level, empty if none provided (device not supported?).
         */
        public Optional<Integer> getTarget() {
            return Optional.ofNullable(target);
        }

        /**
         * Get the duration.
         * @return The duration, empty if none provided (device not supported?).
         */
        public Optional<Integer> getDuration() {
            return Optional.ofNullable(duration);
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new SwitchMultilevelReport(controller, nodeId, this);
        }
    }
}
