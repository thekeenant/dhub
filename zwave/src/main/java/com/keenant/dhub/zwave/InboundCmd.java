package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Byteable;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;
import com.keenant.dhub.zwave.event.CmdEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A command class that we don't send out. They are received and must be parsed
 * via {@link this#parse(ByteList)} and the controller must be notified via events,
 * {@link this#createEvent(Controller, int)}.
 */
public interface InboundCmd extends Byteable {
    /**
     * The list of command parser functions. They all take a {@link ByteList} and
     * return an {@link Optional< InboundCmd >}.
     */
    List<Function<ByteList, Optional<? extends InboundCmd>>> CMD_PARSERS = Arrays.asList(
            BasicCmd::parseReport,
            MultiChannelCmd::parseEndPointReport,
            SwitchBinaryCmd::parseReport,
            SwitchMultilevelCmd::parseReport
    );

    /**
     * Parse an inbound command class.
     * @param data The raw data for the command class, including its ID.
     * @return The new command object, empty if we didn't understand it.
     */
    static Optional<InboundCmd> parse(ByteList data) {
        for (Function<ByteList, Optional<? extends InboundCmd>> parser : CMD_PARSERS) {
            Optional<? extends InboundCmd> cmd = parser.apply(data);
            if (cmd.isPresent()) {
                return Optional.of(cmd.get());
            }
        }

        return Optional.empty();
    }

    /**
     * Create a new event associated with this command and it's data.
     * @param controller The controller this command was associated with.
     * @param nodeId The node id of this command.
     * @return The new command event.
     */
    CmdEvent createEvent(Controller controller, int nodeId);

    @Override
    default ByteList toBytes() {
        throw new UnsupportedOperationException("Inbound command can't be converted back to bytes.");
    }
}
