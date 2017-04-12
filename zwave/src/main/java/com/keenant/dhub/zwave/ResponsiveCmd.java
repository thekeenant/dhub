package com.keenant.dhub.zwave;

public interface ResponsiveCmd<R extends InboundCmd> extends Cmd {
    CmdParser<R> getResponseParser();
}
