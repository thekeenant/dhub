package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.frame.DataFrame;
import com.keenant.dhub.zwave.transaction.Transaction;

public interface Message<Txn extends Transaction> extends DataFrame {
    default Txn createTransaction(ZController controller) {
        return createTransaction(controller, Priority.DEFAULT);
    }

    Txn createTransaction(ZController controller, Priority priority);
}
