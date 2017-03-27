package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.eventbus.EventBus;
import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.logging.Logger;

public class Controller {
    private final SerialPort port;
    private final EventBus eventBus;
    private final PriorityQueue<Transaction> transactions;
    private final Logger log;
    private Transceiver transceiver;
    private Transaction current;

    public Controller(SerialPort port) {
        if (port == null) {
            throw new NullPointerException("Port cannot be null.");
        }
        this.port = port;
        this.eventBus = new EventBus();
        this.transactions = new PriorityQueue<>((o1, o2) -> {
            int val = o2.getPriority().compareTo(o1.getPriority());
            if (val != 0) {
                return val;
            }
            return Long.compare(o1.getCreationTimeNanos(), o2.getCreationTimeNanos());
        });
        this.log = Logging.getLogger(getName());
    }

    /**
     * Called when a new message that is outside of a transaction is received from the controller.
     * @param msg The incoming message.
     */
    void onReceive(IncomingMessage msg) {
        if (msg instanceof ApplicationCommandMsg) {
            ApplicationCommandMsg appMsg = (ApplicationCommandMsg) msg;
            IncomingCmd cmd = appMsg.getCmd().orElse(null);

            if (cmd != null) {
                CmdEvent event = cmd.createEvent(this, appMsg.getNodeId());
                eventBus.post(event);
            }
        }

        IncomingMessageEvent event = msg.createEvent(this);
        eventBus.post(event);
    }

    /**
     * Updates the transaction queue by moving the earliest in the queue
     * to the current, if the current is finished.
     *
     * It will perform transaction start and transaction complete logs and events.
     *
     * @return The new current transaction. Empty if there isn't an ongoing transaction.
     */
    Optional<Transaction> updateCurrent() {
        if (current != null) {
            if (current.isFinished()) {
                log.log(Level.DEV, "Transaction complete: " + current);
                current.setCompletionTimeNanos(System.nanoTime());

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

            log.log(Level.DEV, "Transaction started: " + current);
            txn.setStartTimeNanos(System.nanoTime());

            return Optional.of(txn);
        }
    }

    /**
     * Register a listener to this controller's events.
     * @param listener The listener that contains various event subscriptions.
     */
    public void register(Listener listener) {
        eventBus.register(listener);
    }

    /**
     * Unregister an already registered listener from this controller's events.
     * @param listener The listener.
     */
    public void unregister(Listener listener) {
        eventBus.unregister(listener);
    }

    /**
     * Clear all upcoming transactions. This does not halt the current transaction.
     */
    public void clearQueue() {
        transactions.clear();
    }

    /**
     * Prepares the controller to start a transaction.
     *
     * @param message The message to send.
     * @param priority The priority. Higher priority means earlier in the queue.
     * @param <T> The transaction type.
     * @return The new transaction, which is now added to the queue..
     */
    public <T extends Transaction> T queue(Message<T> message, Priority priority) {
        T txn = message.createTransaction(this, priority);
        queue(txn);
        return txn;
    }

    /**
     * Prepares the controller to start a transaction at the default priority level.
     *
     * @param message The message to send.
     * @param <T> The transaction type.
     * @return The new transaction, which is now added to the queue..
     */
    public <T extends Transaction> T queue(Message<T> message) {
        return queue(message, Priority.DEFAULT);
    }

    /**
     * Prepares the controller to start a transaction.
     *
     * @param txn The transaction.
     */
    public void queue(Transaction txn) {
        transactions.add(txn);
    }

    /**
     * Check if this controller is alive, meaning #{@link this#start()} was called
     * and therefore we are actively *trying* to write and read data to and from the serial port.
     *
     * This doesn't mean we are successfully interacting with the controller.
     *
     * @return True if it is alive, false if otherwise.
     */
    public boolean isAlive() {
        return transceiver != null && transceiver.isAlive();
    }

    /**
     * Open the serial port and start writing and reading on a separate transceiver.
     *
     * Trashes any current transaction. The transaction queue is unaffected.
     *
     * @throws UnsupportedOperationException If the controller is already started.
     */
    public void start() throws UnsupportedOperationException {
        if (isAlive()) {
            throw new UnsupportedOperationException("Controller already started.");
        }

        current = null;
        transceiver = new Transceiver(this, port);
        transceiver.start();
    }

    /**
     * Closes the serial port, stops writing and reading.
     *
     * Trashes any current transaction. The transaction queue is unaffected.
     */
    public void stop() {
        if (!isAlive()) {
            return;
        }

        transceiver.stop();
        transceiver = null;
        current = null;
    }

    /**
     * @return The transceiver if the controller is alive, otherwise, empty.
     */
    public Optional<Transceiver> getTransceiver() {
        return Optional.ofNullable(transceiver);
    }

    /**
     * @return The name of this controller, aka, the system port name.
     */
    public String getName() {
        return port.getSystemPortName();
    }

    @Override
    public String toString() {
        return "Controller(port=" + getName() + ")";
    }
}
