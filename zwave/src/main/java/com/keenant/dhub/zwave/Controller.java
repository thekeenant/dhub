package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.core.util.PrioritizedObject;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.event.CmdEvent;
import com.keenant.dhub.zwave.event.Event;
import com.keenant.dhub.zwave.event.InboundMessageEvent;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.transaction.Transaction;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler.ConsoleLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Controller {
    /**
     * Sorts the transaction queue.
     */
    private static final Comparator<PrioritizedObject<Transaction>> TXN_SORTER = (o1, o2) -> {
        int priority = PrioritizedObject.DESCENDING.compare(o1, o2);

        // Prioritize priorities
        if (priority != 0) {
            return priority;
        }

        // Otherwise order by the time they were queued
        return Long.compare(o1.getObject().getQueuedTimeNanos(), o2.getObject().getQueuedTimeNanos());
    };

    private final SerialPort port;
    private final MBassador<Event> bus;
    private final List<PrioritizedObject<Transaction>> transactions;
    private final Logger log;
    private Transceiver transceiver;
    private Transaction current;

    /**
     * Constructor.
     * @param port The serial port for this controller.
     * @throws NullPointerException If the port is null.
     */
    public Controller(SerialPort port) throws NullPointerException {
        if (port == null) {
            throw new NullPointerException("Port cannot be null.");
        }
        this.port = port;
        this.bus = new MBassador<>(new ConsoleLogger(true));
        this.transactions = new ArrayList<>();
        this.log = Logging.getLogger(getName());
    }

    /**
     * Constructor.
     * @param portName The serial port name for this controller (ex. ttyACM0, s0, ...).
     */
    public Controller(String portName) {
        this(SerialPort.getCommPort(portName));
    }

    /**
     * Called when a new message is received from the controller.
     * @param msg The inbound message.
     */
    public void onReceive(InboundMessage msg) {
        if (msg instanceof ApplicationCommandMsg) {
            ApplicationCommandMsg appMsg = (ApplicationCommandMsg) msg;
            InboundCmd cmd = appMsg.getCmd().orElse(null);

            if (cmd != null) {
                CmdEvent event = cmd.createEvent(this, appMsg.getNodeId());
                bus.post(event).asynchronously();
            }
        }

        InboundMessageEvent event = msg.createEvent(this);
        bus.post(event).asynchronously();
    }

    /**
     * Updates the transaction addToOutboundQueue by moving the earliest in the addToOutboundQueue
     * to the current, if the current is finished.
     *
     * It will perform transaction start and transaction complete logs and events.
     *
     * @return The new current transaction. Empty if there isn't an outbound transaction.
     */
    Optional<Transaction> updateCurrent() {
        if (current != null) {
            if (current.isFinished()) {
                log.log(Level.DEV, "Transaction complete: " + current);
                current.setCompletionTimeNanos(System.nanoTime());

                // Transaction finished event
                TransactionCompleteEvent event = new TransactionCompleteEvent(this, current);
                bus.post(event).asynchronously();

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
            // Move front of the addToOutboundQueue to current, return that.
            Transaction txn = transactions.remove(0).getObject();
            current = txn;
            txn.start();

            log.log(Level.DEV, "Transaction started: " + current);
            txn.setStartTimeNanos(System.nanoTime());

            return Optional.of(txn);
        }
    }

    /**
     * Subscribe a listener to this controller's events.
     * @param listener The listener that contains various event subscriptions.
     */
    public void subscribe(Listener listener) {
        bus.subscribe(listener);
    }

    /**
     * Unsubscribe an already registered listener from this controller's events.
     * @param listener The listener.
     */
    public void unsubscribe(Listener listener) {
        bus.unsubscribe(listener);
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
     * @param txn The transaction.
     * @throws IllegalStateException If the transaction was already started.
     * @throws IllegalArgumentException If the transaction is in the current queue already.
     */
    public void queue(Transaction txn, Priority priority) throws IllegalStateException, IllegalArgumentException {
        // Can't be started already.
        if (txn.isStarted()) {
            throw new IllegalStateException("Transaction already started, cannot be queued.");
        }

        // Can't be queued already.
        for (PrioritizedObject<Transaction> curr : transactions) {
            if (curr.getObject().equals(txn)) {
                throw new IllegalArgumentException("Transaction already queued.");
            }
        }

        // Set the time this transaction was queued.
        txn.setQueuedTimeNanos(System.nanoTime());

        // Add to list and sort
        transactions.add(new PrioritizedObject<>(txn, priority));
        transactions.sort(TXN_SORTER);
    }

    /**
     * Prepares the controller to start a transaction (default priority).
     * @param txn The transaction.
     * @throws IllegalStateException If the transaction was already started.
     * @throws IllegalArgumentException If the transaction is in the current queue already.
     */
    public void queue(Transaction txn) throws IllegalStateException, IllegalArgumentException {
        queue(txn, Priority.DEFAULT);
    }

    /**
     * Prepares the controller to start a transaction.
     *
     * @param message The message to send.
     * @param priority The priority. Higher priority means earlier in the addToOutboundQueue.
     * @param <T> The transaction type.
     * @return The new transaction, which is now added to the addToOutboundQueue..
     */
    public <T extends Transaction> T queue(Message<T> message, Priority priority) {
        T txn = message.createTransaction(this);
        queue(txn, priority);
        return txn;
    }

    /**
     * Prepares the controller to start a transaction at the default priority level.
     *
     * @param message The message to send.
     * @param <T> The transaction type.
     * @return The new transaction, which is now added to the addToOutboundQueue..
     */
    public <T extends Transaction> T queue(Message<T> message) {
        return queue(message, Priority.DEFAULT);
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
     * Trashes any current transaction. The transaction addToOutboundQueue is unaffected.
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
     * Trashes any current transaction. The transaction addToOutboundQueue is unaffected.
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
