package com.keenant.dhub.zwave.frame;

import com.keenant.dhub.util.ByteList;

public class GenericFrame implements Frame {
    private final ByteList bites;

    public GenericFrame(ByteList bites) {
        this.bites = bites;
    }

    @Override
    public ByteList toBytes() {
        return null;
    }
}
