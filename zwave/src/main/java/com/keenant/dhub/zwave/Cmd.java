package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.Byteable;

import java.util.Optional;

/**
 * A Z-Wave command class command.
 */
public interface Cmd<R extends InboundMessage> extends Byteable {
    Optional<MessageParser<R>> getResponseParser();
}
