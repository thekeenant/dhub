package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.transaction.Transaction;
import lombok.ToString;

@ToString
public class TransactionCompleteEvent extends ControllerEvent {
    private final Transaction transaction;

    public TransactionCompleteEvent(ZController controller, Transaction transaction) {
        super(controller);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
