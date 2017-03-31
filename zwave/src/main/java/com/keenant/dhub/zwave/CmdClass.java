package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;
import com.keenant.dhub.zwave.exception.CommandFrameException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface CmdClass {
    BasicCmd BASIC = BasicCmd.INSTANCE;
    MultiChannelCmd MULTI_CHANNEL = MultiChannelCmd.INSTANCE;
    SwitchBinaryCmd SWITCH_BINARY = SwitchBinaryCmd.INSTANCE;
    SwitchMultilevelCmd SWITCH_MULTILEVEL = SwitchMultilevelCmd.INSTANCE;

    List<? extends CmdClass> ALL_CMDS = Arrays.asList(
            BASIC,
            MULTI_CHANNEL,
            SWITCH_BINARY,
            SWITCH_MULTILEVEL
    );

    static Optional<CmdClass> getCmdClass(int id) {
        for (CmdClass cmdClass : ALL_CMDS) {
            if (cmdClass.getId() == id) {
                return Optional.of(cmdClass);
            }
        }
        return Optional.empty();
    }

    InboundCmd parseInboundCmd(ByteList data) throws CommandFrameException;

    byte getId();

}
