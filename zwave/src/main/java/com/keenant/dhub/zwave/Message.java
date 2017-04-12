package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.frame.DataFrame;
import com.keenant.dhub.zwave.transaction.Transaction;

/**
 * A data frame that
 * @param <T>
 */
public interface Message<T extends Transaction> extends DataFrame {
    /**
     * Create a new transaction that starts with this outbound data frame.
     * @param controller The controller this message will be dispatched.
     * @return The new transaction.
     */
    T createTransaction(Controller controller);
}
