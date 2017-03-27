package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.eventbus.EventBus;
import com.keenant.dhub.Controller;
import com.keenant.dhub.logging.Logging;
import com.keenant.dhub.util.Priority;
import com.keenant.dhub.zwave.cmd.Cmd;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.event.Listener;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.messages.IncomingMessage;
import com.keenant.dhub.zwave.messages.Message;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.logging.Logger;

public class ZController extends Controller {
    private final SerialPort port;
    private final EventBus eventBus;
    private final PriorityQueue<Transaction> transactions;
    private final Logger log;
    private final List<Transaction> completedTransactions;
    private ZThread thread;
    private Transaction current;

    public ZController(SerialPort port) {
        this.port = port;
        this.eventBus = new EventBus();
        this.transactions = new PriorityQueue<>((o1, o2) -> {
            int val = o2.getPriority().compareTo(o1.getPriority());
            if (val != 0) {
                return val;
            }
            return Long.compare(o1.getCreationTimeNanos(), o2.getCreationTimeNanos());
        });
        this.log = Logging.getLogger(port.getSystemPortName());
        this.completedTransactions = new ArrayList<>();
    }

    public List<Transaction> getCompletedTransactions() {
        return completedTransactions;
    }

    public ZThread getThread() {
        return thread;
    }

    public void register(Listener listener) {
        eventBus.register(listener);
    }

    public void unregister(Listener listener) {
        eventBus.unregister(listener);
    }

    public <T extends Transaction> T queue(Message<T> message, Priority priority) {
        T txn = message.createTransaction(this, priority);
        queue(txn);
        return txn;
    }

    public <T extends Transaction> T queue(Message<T> message) {
        return queue(message, Priority.DEFAULT);
    }

    public void queue(Transaction txn) {
        if (!isAlive()) {
            throw new UnsupportedOperationException("Controller is not alive. RIP.");
        }
        transactions.add(txn);
    }

    public void onReceive(IncomingMessage msg) {
        if (msg instanceof ApplicationCommandMsg) {
            ApplicationCommandMsg appMsg = (ApplicationCommandMsg) msg;
            Cmd cmd = appMsg.getCommand().orElse(null);

            if (cmd != null) {
                CmdEvent cmdEvent = new CmdEvent(this, appMsg.getNodeId(), cmd);
                eventBus.post(cmdEvent);
            }
        }

        IncomingMessageEvent event = msg.createEvent(this);
        eventBus.post(event);
    }

    public Optional<Transaction> updateCurrent() {
        if (current != null) {
            if (current.isFinished()) {
                log.info("Transaction complete: " + current);
                current.setCompletionTimeNanos(System.nanoTime());
                completedTransactions.add(current);

                // Transaction finished event
                TransactionCompleteEvent event = new TransactionCompleteEvent(this, current);
                eventBus.post(event);

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
            Transaction txn = transactions.poll();
            current = txn;
            txn.start();

            log.info("Transaction started: " + current);
            txn.setStartTimeNanos(System.nanoTime());

            return Optional.of(txn);
        }
    }

    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    public void start() {
        if (isAlive()) {
            throw new UnsupportedOperationException("Controller already started.");
        }

        // Setup the port properly...

        thread = new ZThread(this, port);
        thread.start();
    }

    public void stop() {
        if (!isAlive()) {
            return;
        }

        thread.stop();
        thread = null;
    }

    @Override
    public String toString() {
        return "ZController(port=" + port.getSystemPortName() + ")";
    }
}
