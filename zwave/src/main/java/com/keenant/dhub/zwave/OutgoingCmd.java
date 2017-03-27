package com.keenant.dhub.zwave;

public interface OutgoingCmd extends Cmd {
    boolean isResponseExpected();
}
