package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.frame.DataFrame;
import com.keenant.dhub.zwave.transaction.Transaction;

/**
 * A data frame that
 * @param <Txn>
 */
public interface Message<Txn extends Transaction> extends DataFrame {
    /**
     * Create a new transaction that starts with this outgoing data frame.
     * @param controller The controller this message will be dispatched.
     * @return The new transaction.
     */
    Txn createTransaction(Controller controller);
}
