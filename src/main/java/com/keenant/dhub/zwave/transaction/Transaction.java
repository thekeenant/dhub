package com.keenant.dhub.zwave.transaction;

import com.keenant.dhub.util.Priority;

public interface Transaction {
    boolean isFinished();

    Priority getPriority();
}
