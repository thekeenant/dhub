package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.util.PrioritizedObject;
import com.keenant.dhub.core.util.Priority;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.InboundEncap;
import com.keenant.dhub.zwave.event.*;
import com.keenant.dhub.zwave.messages.ApplicationCommandMsg;
import com.keenant.dhub.zwave.transaction.Transaction;
import com.keenant.dhub.zwave.util.EndPoint;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler.ConsoleLogger;
import net.engio.mbassy.listener.Handler;

import java.util.*;
import java.util.logging.Level;
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
    private final MBassador<ControllerEvent> bus;
    private final List<PrioritizedObject<Transaction>> transactions;
    private final Logger log;
    private Transceiver transceiver;
    private Transaction current;

    /**
     * Constructor.
     * @param port The serial port for this controller.
     * @throws IllegalArgumentException If the port is null.
     */
    public Controller(SerialPort port, Logger log) throws IllegalArgumentException {
        if (port == null) {
            throw new IllegalArgumentException("Port cannot be null.");
        }
        this.port = port;
        this.bus = new MBassador<>(new ConsoleLogger(true));
        this.transactions = Collections.synchronizedList(new ArrayList<>());
        this.log = log;
    }

    /**
     * Constructor.
     * @param portName The serial port name for this controller (ex. ttyACM0, s0, ...).
     */
    public Controller(String portName, Logger log) {
        this(SerialPort.getCommPort(portName), log);
    }

    /**
     * Called when a new message is received from the controller.
     * @param msg The inbound message.
     */
    public void onReceive(InboundMessage msg) {
        // Post command events
        if (msg instanceof ApplicationCommandMsg) {
            ApplicationCommandMsg appMsg = (ApplicationCommandMsg) msg;
            InboundCmd cmd = appMsg.getCmd();

            // Multi channel encap messages contain a command for an endpoint
            if (cmd instanceof InboundEncap) {
                InboundEncap encap = (InboundEncap) cmd;
                EndPoint point = new EndPoint(this, appMsg.getNodeId(), encap.getEndPoint());
                CmdEvent event = encap.getCmd().createEvent(this, point);
                bus.publishAsync(event);
            }

            // All app messages are a command
            EndPoint point = new EndPoint(this, appMsg.getNodeId());
            CmdEvent event = cmd.createEvent(this, point);
            bus.publishAsync(event);
        }

        // Post message events
        InboundMessageEvent event = msg.createEvent(this);
        bus.publishAsync(event);
    }

    /**
     * Updates the transaction queue by moving the earliest in the queue
     * to the current, if the current is finished.
     *
     * It will perform transaction start and transaction complete logs and events.
     *
     * @return The new current transaction. Empty if there isn't an outbound transaction.
     */
    Optional<Transaction> updateCurrent() {
        // Todo: This was my laziness...

        if (current != null) {
            if (current.isComplete()) {
                log.finer("Transaction complete: " + current);
                current.setCompletionTimeNanos(System.nanoTime());

                // Transaction finished event
                TransactionCompleteEvent event = new TransactionCompleteEvent(this, current);
                bus.publishAsync(event);

                // Clear current if it is finished.
                current = null;
            }
            else if (current.isTimeout()) {
                log.log(Level.WARNING, "Transaction timeout: " + current);
                TransactionTimeoutEvent event = new TransactionTimeoutEvent(this, current);
                bus.publishAsync(event);
                current = null;
                // Todo: txn.recreate() to resend it?
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

        // Move front of the queue to current, return that.
        Transaction txn = transactions.remove(0).getObject();
        txn.setStartTimeNanos(System.nanoTime());
        current = txn;

        log.finer("Transaction started: " + current);
        txn.start();

        // Transaction start event
        TransactionStartEvent event = new TransactionStartEvent(this, current);
        bus.publishAsync(event);

        return Optional.of(txn);
    }

    /**
     * Listen to a single event on this controller.
     *
     * This is not really recommended for production usage, as it actually listens to all controller events,
     * checking each one to see if it is of the type you provide. Best to listen to
     * specific event via subscribing a listener, see {@link this#subscribe(ControllerListener)}.
     *
     * @param type The type of event to listen to. It can be as generic as a {@link ControllerEvent}, or
     *             as specific as you wish.
     * @param handler The handler, which will be called upon receiving the event.
     * @param <T> The type of the event.
     * @return The newly created listener (anonymous object). You can provide this object
     *         to the #{@link this#unsubscribe(ControllerListener)} method to stop this handler from getting
     *         events.
     */
    public <T extends ControllerEvent> ControllerListener listen(Class<T> type, ControllerEventHandler<T> handler) {
        // Generic listener which handles all controller events.
        ControllerListener listener = new ControllerListener() {
            @Handler
            @SuppressWarnings("unchecked")
            public void onEvent(ControllerEvent event) {
                if (!type.isInstance(event)) {
                    return;
                }

                handler.handle(this, (T) event);
            }
        };

        // Subscribe it
        subscribe(listener);

        return listener;
    }

    /**
     * Subscribe a listener to this controller's events.
     * @param listener The listener that contains various event subscriptions.
     */
    public void subscribe(ControllerListener listener) {
        bus.subscribe(listener);
    }

    /**
     * Unsubscribe an already registered listener from this controller's events.
     * @param listener The listener.
     */
    public void unsubscribe(ControllerListener listener) {
        bus.unsubscribe(listener);
    }

    /**
     * Clear all upcoming transactions. This does not halt the current transaction.
     */
    public void clearQueue() {
        // Todo: Is synchronized necessary
        synchronized (transactions) {
            transactions.clear();
        }
    }

    /**
     * Prepares the controller to start a transaction.
     *
     * @param txn The transaction.
     * @throws IllegalStateException If the transaction was already queued or started.
     */
    public void send(Transaction txn, Priority priority) throws IllegalStateException, IllegalArgumentException {
        // Can't be started already
        if (txn.isStarted()) {
            throw new IllegalStateException("Transaction already started, cannot be queued.");
        }

        // Can't be queued already.
        for (PrioritizedObject<Transaction> curr : transactions) {
            if (curr.getObject().equals(txn)) {
                throw new IllegalStateException("Transaction is currently queued.");
            }
        }

        // Set the time this transaction was queued.
        txn.setQueuedTimeNanos(System.nanoTime());

        // Add to list and sort
        transactions.add(new PrioritizedObject<>(txn, priority));
        synchronized (transactions) {
            transactions.sort(TXN_SORTER);
        }
    }

    /**
     * Prepares the controller to start a transaction (default priority).
     * @param txn The transaction.
     * @throws IllegalStateException If the transaction was already started.
     * @throws IllegalArgumentException If the transaction is in the current queue already.
     */
    public void send(Transaction txn) throws IllegalStateException, IllegalArgumentException {
        send(txn, Priority.DEFAULT);
    }

    /**
     * Prepares the controller to start a transaction.
     *
     * @param message The message to send.
     * @param priority The priority. Higher priority means earlier in the queue.
     * @param <T> The transaction type.
     * @return The new transaction, which is now added to the queue..
     */
    public <T extends Transaction> T send(Message<T> message, Priority priority) {
        T txn = message.createTransaction(this);
        send(txn, priority);
        return txn;
    }

    /**
     * Prepares the controller to start a transaction at the default priority level.
     *
     * @param message The message to send.
     * @param <T> The transaction type.
     * @return The new transaction, which is now added to the queue..
     */
    public <T extends Transaction> T send(Message<T> message) {
        return send(message, Priority.DEFAULT);
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
     * @throws IllegalStateException If the controller is already started.
     */
    public void start() throws IllegalStateException {
        if (isAlive()) {
            throw new IllegalStateException("Controller already started.");
        }

        current = null;
        transceiver = new Transceiver(this, port, log);
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
     * @return The transaction currently being transmitted/received.
     */
    public Optional<Transaction> getCurrent() {
        return Optional.ofNullable(current);
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
