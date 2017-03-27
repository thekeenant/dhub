package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.IncomingCmd;
import com.keenant.dhub.zwave.OutgoingCmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import lombok.ToString;

import java.util.Optional;

@ToString
public class BasicCmd {
    private static final byte ID = (byte) 0x20;
    private static final byte SET = (byte) 0x01;
    private static final byte GET = (byte) 0x02;
    private static final byte REPORT = (byte) 0x03;

    private static final Get GET_INSTANCE = new Get();

    public static Set set(int value) {
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
            int value = data.get(2) & 0xFF;
            return Optional.of(new Report(value));
        }

        return Optional.empty();
    }

    @ToString
    public static class Set implements OutgoingCmd {
        private final int value;

        private Set(int value) {
            this.value = value;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, SET, value);
        }

        @Override
        public boolean isResponseExpected() {
            return false;
        }
    }

    @ToString
    public static class Get implements OutgoingCmd {
        private Get() {

        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, GET);
        }

        @Override
        public boolean isResponseExpected() {
            return true;
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
