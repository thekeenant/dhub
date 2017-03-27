package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.frame.DataFrame;
import com.keenant.dhub.zwave.transaction.Transaction;

public interface Message<Txn extends Transaction> extends DataFrame {
    default Txn createTransaction() {
        return createTransaction(Priority.DEFAULT);
    }

    Txn createTransaction(Priority priority);
}
