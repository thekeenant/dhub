package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.Controller;
import com.keenant.dhub.zwave.messages.InitDataMsg;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.VersionMsg;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.*;

public class ZController extends Controller {
    private final SerialPort port;
    private ZStick stick;

    private final List<Transaction> transactions;
    private Transaction current;

    public ZController(SerialPort port) {
        this.port = port;
        transactions = new ArrayList<>();
    }

    public ZStick getStick() {
        return stick;
    }

    public void queue(Transaction txn) {
        transactions.add(txn);
    }

    public Optional<Transaction> getCurrent() {
        if (current != null) {
            if (current.isFinished()) {
                // Clear current if it is finished.
                current = null;
            }
            else {
                // Current is valid and still being processed.
                return Optional.of(current);
            }
        }

        if (transactions.isEmpty()) {
            // Nothing queued either!
            return Optional.empty();
        }
        else {
            // Move front of the queue to current, return that.
            Transaction txn = transactions.remove(0);
            current = txn;
            txn.start();
            return Optional.of(txn);
        }
    }

    public void start() {
        stick = new ZStick(this, port);
        stick.start();

        Transaction vers = new ReqResTransaction<>(new VersionMsg());
        Transaction mem = new ReqResTransaction<>(new MemoryGetIdMsg());
        Transaction init = new ReqResTransaction<>(new InitDataMsg());

        queue(vers);
        queue(mem);
        queue(init);
    }

    public void stop() {
        stick.stop();
    }
}
