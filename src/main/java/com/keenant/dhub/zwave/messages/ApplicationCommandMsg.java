package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.zwave.cmd.Cmd;

/**
 * ZW->PC: REQ | 0x04 | rxStatus | sourceNode | cmdLength | pCmd[] | rssiVal
 */
public class ApplicationCommandMsg {
    private static final byte ID = (byte) 0x04;

    private final byte status;
    private final byte nodeId;
    private final Cmd command;

    public ApplicationCommandMsg(byte status, byte nodeId, Cmd command) {
        this.status = status;
        this.nodeId = nodeId;
        this.command = command;
    }

    public static Optional<>
}
