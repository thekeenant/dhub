package com.keenant.dhub.examples;

import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelEndPointReportEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.transaction.Transaction;
import net.engio.mbassy.listener.Handler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Some Z-Wave library examples.
 */
public class ZWaveExamples {
    private static final int NODE_ID = 35; // arbitrary, pick one that is your device
    private static final int MC_NODE_ID = 34; // some multi channel node id

    private static Controller controller;

    public static void main(String[] args) throws Exception {
        Logging.setLevel(Level.INFO);

        controller = new Controller("ttyACM0");
        controller.start();

        System.out.println("basicSetTest start");
        basicSetTest(NODE_ID);
        System.out.println("basicSetTest done");
        System.out.println();


        System.out.println("basicGetReportTest start");
        basicGetReportTest(NODE_ID);
        System.out.println("basicGetReportTest done");
        System.out.println();

        System.out.println("multiChannelTest start");
        multiChannelTest(MC_NODE_ID);
        System.out.println("multiChannelTest done");
    }

    /**
     * Basic set demos
     */
    private static void basicSetTest(int nodeId) {
        // Set to 50%.
        controller.send(new SendDataMsg(nodeId, BasicCmd.setPercent(0.5)));

        // Turn off.
        controller.send(new SendDataMsg(nodeId, BasicCmd.set(0)));

        // Then turn it back to 50% via basic on command, equivalent to BasicCmd.set(255).
        controller.send(new SendDataMsg(nodeId, BasicCmd.setOn()));

        // Turn to value 25 (out of 99).
        controller.send(new SendDataMsg(nodeId, BasicCmd.set(25)));

        // Another way to turn off
        Transaction last = controller.send(new SendDataMsg(nodeId, BasicCmd.setOff()));

        System.out.print("  ");
        while (!last.await(500)) {
            System.out.print(".");
        }
        System.out.println();

        // Wait until the last transaction is done before the other tests start.
        last.await(10000);
    }

    /**
     * Basic get/report demos.
     */
    private static void basicGetReportTest(int nodeId) throws Exception {
        System.out.println("  Thread: " + Thread.currentThread());

        // Here we make a listener. 99% of the time it shouldn't be anonymous, like it is here.
        // Anything can be a listener, it's just an interface.
        Listener listener = new Listener() {
            // Subscribe to BasicReportEvent events (the method name does not matter).
            @Handler
            public void onBasicReport(BasicReportEvent event) {
                int nodeId = event.getNodeId();
                BasicCmd.Report report = event.getCmd();

                // We are async, on a different thread (compare to earlier print stmt).
                System.out.println("    Thread: " + Thread.currentThread());

                System.out.println("    Received basic report command...");
                System.out.println("    Node #" + nodeId + " reports percent: " + report.getPercent());
            }
        };

        // Tell the controller about this listener so it passes events to it.
        controller.subscribe(listener);

        // Let's set it to 25% first.
        System.out.println("  Queueing basic set command...");
        Transaction txn = controller.send(new SendDataMsg(nodeId, BasicCmd.setPercent(0.25)));

        // We force the previous transaction to finish.
        System.out.print("  .");
        while (!txn.await(500)) {
            System.out.print(".");
        }
        System.out.println();

        // Tell a device to send us a report!
        System.out.println("  Queueing basic get command...");
        Transaction await = controller.send(new SendDataMsg(nodeId, BasicCmd.get()));
        await.await();

        // Turn back off
        System.out.println("  Queueing basic set command (back to off)...");
        Transaction last = controller.send(new SendDataMsg(nodeId, BasicCmd.setOff()));
        last.await();
    }

    /**
     * Test multi channel node.
     */
    private static void multiChannelTest(int nodeId) throws InterruptedException {
        // Count will be set to the number of endpoints.
        AtomicInteger count = new AtomicInteger();
        AtomicBoolean await = new AtomicBoolean(true);

        controller.subscribe(new Listener() {
            @Handler
            public void onEndPointReport(MultiChannelEndPointReportEvent event) {
                int endPointCount = event.getCmd().getEndPointCount();
                System.out.println("  " + endPointCount + " endpoints");

                // Set the count.
                count.set(endPointCount);

                // Notify other thread to stop waiting.
                await.set(false);
            }
        });

        // Queue the get message, so we get a report message back.
        controller.send(new SendDataMsg(nodeId, MultiChannelCmd.endPointGet()));

        // Wait until we know how many endpoints there are...
        while (await.get()) {
            Thread.sleep(100);
        }

        Transaction last = null;

        // Turn on and off each endpoint.
        for (int i = 1; i < count.get() + 1; i++) {
            // On
            Cmd cmd = MultiChannelCmd.encap(i, SwitchBinaryCmd.set(true));
            controller.send(new SendDataMsg(nodeId, cmd));

            // Off
            cmd = MultiChannelCmd.encap(i, SwitchBinaryCmd.set(false));
            last = controller.send(new SendDataMsg(nodeId, cmd));
        }

        // Wait until last is done
        if (last != null) {
            System.out.print("  ");
            while (!last.await(500)) {
                System.out.print(".");
            }
            System.out.println();
        }
    }
}
