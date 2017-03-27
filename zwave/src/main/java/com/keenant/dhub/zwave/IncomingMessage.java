package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.messages.ApplicationUpdateMsg;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public interface IncomingMessage {
    /**
     * List of parsers. They all take two arguments, a {@link ByteList} and a {@link DataFrameType},
     * and return an {@link Optional<IncomingMessage>}.
     */
    List<BiFunction<ByteList, DataFrameType, Optional<? extends IncomingMessage>>> MSG_PARSERS = Arrays.asList(
            ApplicationCommandMsg::parse,
            ApplicationUpdateMsg::parse
    );

    /**
     * Parse incoming data to an object.
     * @param data The raw data.
     * @param type The data frame type.
     * @return The parsed message, or empty if we don't understand it.
     */
    static Optional<IncomingMessage> parse(ByteList data, DataFrameType type) {
        for (BiFunction<ByteList, DataFrameType, Optional<? extends IncomingMessage>> parser : MSG_PARSERS) {
            try {
                Optional<? extends IncomingMessage> opt = parser.apply(data, type);
                if (opt.isPresent()) {
                    // Optional<? extends IncomingMessage> to Optional<IncomingMessage>
                    return Optional.of(opt.get());
                }
            } catch (Exception e) {

            }
        }
        return Optional.empty();
    }

    /**
     * @return The data frame type.
     */
    DataFrameType getType();

    /**
     * Create a new message event associated with this message.
     * @param controller The controller this message was received on.
     * @return The new message event.
     */
    IncomingMessageEvent createEvent(Controller controller);
}
