package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IncomingCmd extends Cmd {
    List<Function<ByteList, Optional<? extends IncomingCmd>>> CMD_PARSERS = Arrays.asList(
            BasicCmd::parse,
            SwitchBinaryCmd::parse
    );

    static Optional<IncomingCmd> parse(ByteList data) {
        for (Function<ByteList, Optional<? extends IncomingCmd>> parser : CMD_PARSERS) {
            Optional<? extends IncomingCmd> cmd = parser.apply(data);
            if (cmd.isPresent()) {
                return Optional.of(cmd.get());
            }
        }

        return Optional.empty();
    }

    CmdEvent createEvent(Controller controller, int nodeId);
}
