package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.transaction.Transaction;
import lombok.ToString;

@ToString
public class TransactionCompleteEvent extends ControllerEvent {
    private final Transaction transaction;

    public TransactionCompleteEvent(Controller controller, Transaction transaction) {
        super(controller);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
