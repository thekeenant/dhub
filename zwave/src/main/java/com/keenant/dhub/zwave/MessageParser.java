package com.keenant.dhub.zwave;

import java.util.Optional;

@FunctionalInterface
public interface MessageParser<T extends InboundMessage> {
    Optional<T> parseMessage(UnknownMessage msg);
}
