package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.util.ByteList;
import lombok.ToString;

import java.util.Optional;

@ToString
public class BasicCmd {
    private static final byte ID = (byte) 0x20;
    private static final byte SET = (byte) 0x01;
    private static final byte GET = (byte) 0x02;
    private static final byte REPORT = (byte) 0x03;

    private static final Get GET_INSTANCE = new Get();

    public static Set set(byte value) {
        return new Set(value);
    }

    public static Set set(int value) {
        return set((byte) value);
    }

    public static Get get() {
        return GET_INSTANCE;
    }

    public static Optional<Cmd> parse(ByteList data) {
        if (ID != data.get(0)) {
            return Optional.empty();
        }

        byte type = data.get(1);

        if (type == REPORT) {
            byte value = data.get(2);
            return Optional.of(new Report(value));
        }

        return Optional.empty();
    }

    @ToString
    public static class Set implements Cmd {
        private final byte value;

        private Set(byte value) {
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
    public static class Get implements Cmd {
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
    public static class Report implements Cmd {
        private final byte value;

        private Report(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        @Override
        public ByteList toBytes() {
            return new ByteList(ID, REPORT);
        }

        @Override
        public boolean isResponseExpected() {
            return false;
        }
    }
}
