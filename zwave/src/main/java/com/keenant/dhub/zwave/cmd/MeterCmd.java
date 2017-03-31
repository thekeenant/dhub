package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.exception.CommandFrameException;

public class MeterCmd implements CmdClass {
    @Override
    public InboundCmd parseInboundCmd(ByteList data) throws CommandFrameException {
        return null;
    }

    @Override
    public byte getId() {
        return 0;
    }
}
