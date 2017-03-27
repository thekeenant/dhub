package com.keenant.dhub.zwave.cmd;

public interface OutgoingCmd extends Cmd {
    boolean isResponseExpected();
}
