package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.SwitchBinaryReportEvent;
import lombok.ToString;

import java.util.Optional;

@ToString
public class SwitchBinaryCmd {
    private static final byte ID = (byte) 0x25;
    private static final byte SET = (byte) 0x01;
    private static final byte GET = (byte) 0x02;
    private static final byte REPORT = (byte) 0x03;

    private static final Get GET_INSTANCE = new Get();

    public static Set set(boolean value) {
        return new Set(value);
    }

    public static Get get() {
        return GET_INSTANCE;
    }

    public static Optional<IncomingCmd> parse(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        byte type = data.get(1);

        if (type == REPORT) {
            boolean value = data.get(2) == (byte) 0x01;
            return Optional.of(new Report(value));
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
            return new ByteList(ID, SET, value ? (byte) 0xFF : (byte) 0x00, 0x00);
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
        private final boolean value;

        private Report(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, REPORT);
        }

        @Override
        public CmdEvent createEvent(Controller controller, int nodeId) {
            return new SwitchBinaryReportEvent(controller, nodeId, this);
        }
    }
}
