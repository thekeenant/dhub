package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.util.ByteList;
import lombok.ToString;

@ToString
public class BasicSetCmd implements Cmd {
    private final byte value;

    public BasicSetCmd(byte value) {
        this.value = value;
    }

    public BasicSetCmd(int value) {
        this((byte) value);
    }

    @Override
    public ByteList toBytes() {
        return new ByteList((byte) 0x20, (byte) 0x01, value);
    }
}
