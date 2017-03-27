package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.Controller;
import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.frame.DataFrameType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public interface IncomingMessage {
    /**
     * A list of all the possible parser functions.
     *
     * Beautiful Java, I know 'o_O
     */
    List<BiFunction<ByteList, DataFrameType, Optional<? extends IncomingMessage>>> ALL_PARSERS = Arrays.asList(
            ApplicationCommandMsg::parse
    );

    static Optional<IncomingMessage> parse(ByteList data, DataFrameType type) {
        for (BiFunction<ByteList, DataFrameType, Optional<? extends IncomingMessage>> parser : ALL_PARSERS) {
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

    IncomingMessageEvent createEvent(ZController controller);
}
