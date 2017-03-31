package com.keenant.dhub.zwave;

import java.util.Optional;

/**
 * Simple interface for parsing an inbound data frame/message.
 * @param <T> The expected data frame output.
 */
@FunctionalInterface
public interface MessageParser<T extends InboundMessage> {
    /**
     * Attempt to parse the message.
     * @param msg The inbound, unknown message.
     * @return The parsed data frame, or empty if this parser does not apply to it.
     */
    Optional<T> parseMessage(UnknownMessage msg);
}
