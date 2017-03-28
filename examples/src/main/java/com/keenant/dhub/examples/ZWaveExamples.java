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
        sleep(1000);

        basicSetTest(NODE_ID);
        basicGetReportTest(NODE_ID);
        multiChannelTest(MC_NODE_ID);
    }

    /**
     * Basic set demos
     */
    private static void basicSetTest(int nodeId) {
        System.out.println("basicSetTest start");

        // Set to 50%.
        controller.send(SendDataMsg.get(nodeId, BasicCmd.setPercent(0.5)));
        sleep(2000);

        // Turn off.
        controller.send(SendDataMsg.get(nodeId, BasicCmd.set(0)));
        sleep(2000);

        // Then turn it back to 50% via basic on command, equivalent to BasicCmd.set(255).
        controller.send(SendDataMsg.get(nodeId, BasicCmd.setOn()));
        sleep(2000);

        // Turn to value 25 (out of 99).
        controller.send(SendDataMsg.get(nodeId, BasicCmd.set(25)));
        sleep(2000);

        // Another way to turn off
        controller.send(SendDataMsg.get(nodeId, BasicCmd.setOff()));
        sleep(2000);

        System.out.println("done\n");
    }

    /**
     * Basic get/report demos.
     */
    private static void basicGetReportTest(int nodeId) throws Exception {
        System.out.println("basicGetReportTest start");
        System.out.println("  Thread: " + Thread.currentThread());

        AtomicBoolean await = new AtomicBoolean(true);

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

                await.set(false);
            }
        };

        // Tell the controller about this listener so it passes events to it.
        controller.subscribe(listener);

        // Let's set it to 25% first.
        System.out.println("  Queueing basic set command...");
        controller.send(SendDataMsg.get(nodeId, BasicCmd.setPercent(0.25)));
        sleep(2000);

        // Tell a device to send us a report!
        System.out.println("  Queueing basic get command...");
        controller.send(SendDataMsg.get(nodeId, BasicCmd.get()));

        // Wait until we get the report back!
        while (await.get()) {
            sleep(100);
        }

        // Turn back off
        System.out.println("  Queueing basic set command (back to off)...");
        controller.send(SendDataMsg.get(nodeId, BasicCmd.setOff()));
        sleep(2000);

        System.out.println("done\n");
    }

    /**
     * Test multi channel node.
     */
    private static void multiChannelTest(int nodeId) {
        System.out.println("multiChannelTest start");

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
        controller.send(SendDataMsg.get(nodeId, MultiChannelCmd.endPointGet()));

        // Wait until we know how many endpoints there are...
        while (await.get()) {
            sleep(100);
        }

        // Turn on and off each endpoint.
        for (int i = 1; i < count.get() + 1; i++) {
            // On
            Cmd cmd = MultiChannelCmd.encap(i, SwitchBinaryCmd.set(true));
            controller.send(SendDataMsg.get(nodeId, cmd));
            sleep(100);

            // Off
            cmd = MultiChannelCmd.encap(i, SwitchBinaryCmd.set(false));
            controller.send(SendDataMsg.get(nodeId, cmd));
            sleep(100);
        }

        System.out.println("done\n");
    }

    /**
     * Sleep for a specified duration.
     * @param duration Duration in milliseconds.
     */
    private static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {

        }
    }
}
