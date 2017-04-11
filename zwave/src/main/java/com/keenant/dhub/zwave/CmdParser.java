package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.exception.CommandFrameException;

public interface CmdParser<I extends InboundCmd> {
    /**
     * Parse a command that we receive, typically from a node via
     * {@link com.keenant.dhub.zwave.messages.ApplicationCommandMsg}.
     *
     * @param data The command data, excluding the first byte which would be the command id.
     * @return The parsed command.
     * @throws CommandFrameException If the command was unable to be processed.
     */
    I parseInboundCmd(ByteList data) throws CommandFrameException;
}
