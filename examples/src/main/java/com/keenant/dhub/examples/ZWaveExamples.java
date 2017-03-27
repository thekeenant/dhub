package com.keenant.dhub.examples;

import com.google.common.eventbus.Subscribe;
import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.Transaction;

/**
 * Some Z-Wave library examples.
 */
public class ZWaveExamples {
    private static final int NODE_ID = 35; // arbitrary, pick one that is your device
    private static Controller controller;

    public static void main(String[] args) throws Exception {
        Logging.setLevel(Level.INFO);

        controller = new Controller("ttyACM0");
        controller.start();

        basicGetReportTest();

        basicSetTest();
    }

    /**
     * Basic set demos
     */
    private static void basicSetTest() {
        System.out.println("basicSetTest start");

        // Set to 50%
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setPercent(0.5)));

        // Turn off.
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.set(0)));

        // Then turn it back to 50% via basic on command, equivalent to BasicCmd.set(255).
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setOn()));

        // Turn to value 25 (out of 99).
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.set(25)));

        // Another way to turn off
        Transaction last = controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setOff()));

        // Wait until the last transaction is done before the other tests start.
        last.await(10000);

        System.out.println("basicSetTest done");
    }

    /**
     * Basic get/report demos.
     */
    private static void basicGetReportTest() throws Exception {
        System.out.println("basicGetReportTest start");

        // Here we make a listener. 99% of the time it shouldn't be anonymous, like it is here.
        // Anything can be a listener, it's just an interface.
        Listener listener = new Listener() {
            // Subscribe to BasicReportEvent events (the method name does not matter).
            @Subscribe
            public void onBasicReport(BasicReportEvent event) {
                int nodeId = event.getNodeId();
                BasicCmd.Report report = event.getCmd();

                System.out.println("  Received basic report command...");
                System.out.println("  Node #" + nodeId + " reports percent: " + report.getPercent());
            }
        };

        // Tell the controller about this listener so it passes events to it.
        controller.register(listener);

        // Let's set it to 25% first
        System.out.println("  Queueing basic set command...");
        Transaction txn = controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setPercent(0.25)));

        // We force the previous transaction to finish
        txn.await(10000);

        // Tell a device to send us a report!
        System.out.println("  Queueing basic get command...");
        Transaction last = controller.queue(new SendDataMsg(NODE_ID, BasicCmd.get()));
        last.await(10000);

        // Once it reports back, the method in the listener will be called - if everything goes right.
        Thread.sleep(100); // this is just to make the print statements appear in order
        System.out.println("basicGetReportTest done");
    }
}
