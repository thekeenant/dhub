package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.Controller;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.Optional;
import java.util.PriorityQueue;

public class ZController extends Controller {
    private final SerialPort port;
    private ZStick stick;

    private final PriorityQueue<Transaction> transactions;
    private Transaction current;

    public ZController(SerialPort port) {
        this.port = port;
        transactions = new PriorityQueue<>((o1, o2) -> o2.getPriority().compareTo(o1.getPriority()));
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
            return Optional.of(current = transactions.poll());
        }
    }

    public void start() {
        stick = new ZStick(this, port);
        stick.start();
    }

    public void stop() {
        stick.stop();
    }
}
