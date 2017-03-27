package com.keenant.dhub.examples;

import com.google.common.eventbus.Subscribe;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import com.keenant.dhub.zwave.messages.SendDataMsg;

/**
 * Some Z-Wave library examples.
 */
public class ZWaveExamples {
    private static final int NODE_ID = 35; // arbitrary, pick one that is your device
    private static Controller controller;

    public static void main(String[] args) {
        controller = new Controller("ttyACM0");
        controller.start();

        basicSetTest();

        basicGetReportTest();
    }

    /**
     * Basic set demos
     */
    private static void basicSetTest() {
        // Set to 50%
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setPercent(0.5)));

        // Then turn it off
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setOff()));

        // Then turn it back to 50% via basic on command, equivalent to BasicCmd.set(255).
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.setOn()));

        // Turn to value 35 (out of 99).
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.set(35)));
    }

    /**
     * Basic get/report demos.
     */
    private static void basicGetReportTest() {
        // Here we make a listener. 99% of the time it shouldn't be anonymous, like it is here.
        // Anything can be a listener, it's just an interface.
        Listener listener = new Listener() {
            // Subscribe to BasicReportEvent events (the method name does not matter).
            @Subscribe
            public void onBasicReport(BasicReportEvent event) {
                int nodeId = event.getNodeId();
                BasicCmd.Report report = event.getCmd();

                System.out.println("Received basic report command...");
                System.out.println("Node #" + nodeId + " reports percent: " + report.getPercent());
            }
        };

        // Tell the controller about this listener so it passes events to it.
        controller.register(listener);

        // Tell a device to send us a report!
        System.out.println("Queueing basic get command...");
        controller.queue(new SendDataMsg(NODE_ID, BasicCmd.get()));

        // Once it reports back, the method in the listener will be called - if everything goes right.
        // All done!
    }
}
