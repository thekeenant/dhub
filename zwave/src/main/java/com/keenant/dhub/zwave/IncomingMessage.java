package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public interface IncomingMessage {
    List<BiFunction<ByteList, DataFrameType, Optional<? extends IncomingMessage>>> MSG_PARSERS = Arrays.asList(
            ApplicationCommandMsg::parse
    );

    static Optional<IncomingMessage> parse(ByteList data, DataFrameType type) {
        for (BiFunction<ByteList, DataFrameType, Optional<? extends IncomingMessage>> parser : MSG_PARSERS) {
            Optional<? extends IncomingMessage> opt = parser.apply(data, type);
            if (opt.isPresent()) {
                // Optional<? extends IncomingMessage> to Optional<IncomingMessage>
                return Optional.of(opt.get());
            }
        }
        return Optional.empty();
    }

    /**
     * @return The data frame type.
     */
    DataFrameType getType();

    IncomingMessageEvent createEvent(Controller controller);
}
