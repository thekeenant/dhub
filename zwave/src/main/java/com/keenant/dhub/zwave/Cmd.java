package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.util.Byteable;

import java.util.Optional;

/**
 * A Z-Wave command class command.
 */
public interface Cmd<R extends InboundCmd> extends Byteable {
    Optional<CmdParser<R>> getResponseParser();
}
