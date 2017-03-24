package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.util.ByteList;
import lombok.ToString;

@ToString
public class BasicGetCmd implements Cmd {
    public BasicGetCmd() {

    }

    @Override
    public ByteList toBytes() {
        return new ByteList((byte) 0x20, (byte) 0x00);
    }
}
