package com.keenant.dhub.zwave.cmd;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.util.Byteable;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.event.CmdEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A ZWave command.
 */
public interface Cmd extends Byteable {
    List<Function<ByteList, Optional<Cmd>>> CMD_PARSERS = Arrays.asList(
            BasicCmd::parse,
            SwitchBinaryCmd::parse
    );

    static Optional<Cmd> parse(ByteList data) {
        for (Function<ByteList, Optional<Cmd>> parser : CMD_PARSERS) {
            Optional<Cmd> cmd = parser.apply(data);
            if (cmd.isPresent()) {
                return cmd;
            }
        }

        return Optional.empty();
    }
}
