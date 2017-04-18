package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.util.ByteList;
import com.keenant.dhub.zwave.util.EndPoint;

import java.util.Optional;

/**
 * A command class that we don't send out. They are received and must be parsed
 * via {@link this#parse(ByteList)} and the controller must be notified via events,
 * {@link this#createEvent(Controller, int)}.
 */
public interface InboundCmd {
    /**
     * Parse an inbound command class.
     * @param data The raw data for the command class, including its ID.
     * @return The new command object, empty if we didn't understand it.
     */
    static Optional<InboundCmd> parse(ByteList data) {
        ByteList withoutCmd = data.subList(1, data.size());
        Optional<CmdClass> cmdClass = CmdClass.getCmdClass(data.get(0));

        if (cmdClass.isPresent()) {
            InboundCmd converted = cmdClass.get().parseInboundCmd(withoutCmd);
            return Optional.of(converted);
        }

        return Optional.empty();
    }

    /**
     * Create a new event associated with this command and it's data.
     * @param controller The controller this command was associated with.
     * @param endPoint The node id of this command.
     * @return The new command event.
     */
    CmdEvent createEvent(Controller controller, EndPoint endPoint);
}
