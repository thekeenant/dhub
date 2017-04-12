package com.keenant.dhub.examples;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.ControllerListener;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.BasicCmd.Get;
import com.keenant.dhub.zwave.cmd.BasicCmd.Report;
import com.keenant.dhub.zwave.cmd.BasicCmd.Set;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.Encap;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd.ResponsiveEncap;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import com.keenant.dhub.zwave.event.cmd.MultiChannelEndPointReportEvent;
import com.keenant.dhub.zwave.messages.AddNodeMsg;
import com.keenant.dhub.zwave.messages.DataMsg.SendDataMsg;
import com.keenant.dhub.zwave.messages.DataMsg.SendReceiveDataMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg;
import com.keenant.dhub.zwave.messages.NodeListMsg.Reply;
import com.keenant.dhub.zwave.messages.RemoveNodeMsg;
import com.keenant.dhub.zwave.transaction.AddNodeTxn;
import com.keenant.dhub.zwave.transaction.RemoveNodeTxn;
import com.keenant.dhub.zwave.transaction.ReplyTxn;
import net.engio.mbassy.listener.Handler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Some Z-Wave library examples.
 */
public class ZWaveExamples {
    private static final int NODE_ID = 3; // arbitrary, pick one that is your device
    private static final int MC_NODE_ID = 2; // some multi channel node id

    private static Controller controller;

    public static void main(String[] args) throws Exception {
        Logging.setLevel(Level.INFO);

        controller = new Controller(SerialPort.getCommPorts()[1]);
        controller.start();

        System.out.println(controller);

        ReplyTxn<Reply> txn = controller.send(new NodeListMsg());
        txn.await();
        if (txn.getReply().isPresent()) {
            System.out.println("Nodes on network: " + txn.getReply().get().getNodeIds());
        }
        else {
            System.out.println("Unable to get nodes on network...");
            return;
        }

//        addRemoveNodeTest();
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
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.setPercent(0.5)));
        sleep(2000);

        // Turn off.
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.set(0)));
        sleep(2000);

        // Then turn it back to 50% via basic on command, equivalent to BasicCmd.set(255).
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.setOn()));
        sleep(2000);

        // Turn to value 25 (out of 99).
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.set(25)));
        sleep(2000);

        // Another way to turn off
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.setOff()));
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
        ControllerListener listener = new ControllerListener() {
            // Subscribe to BasicReportEvent events (the method name does not matter).
            @Handler
            public void onBasicReport(BasicReportEvent event) {
                int nodeId = event.getEndPoint().getNodeId();
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
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.setPercent(0.33)));
        sleep(2000);

        // Tell a device to send us a report!
        System.out.println("  Queueing basic get command...");
        controller.send(new SendReceiveDataMsg<>(nodeId, CmdClass.BASIC.get()));

        // Wait until we get the report back!
        while (await.get()) {
            sleep(100);
        }

        // Turn back off
        System.out.println("  Queueing basic set command (back to off)...");
        controller.send(new SendDataMsg<>(nodeId, CmdClass.BASIC.setOff()));
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

        controller.subscribe(new ControllerListener() {
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
        controller.send(new SendReceiveDataMsg<>(nodeId, CmdClass.MULTI_CHANNEL.endPointGet()));

        // Wait until we know how many endpoints there are...
        while (await.get()) {
            sleep(100);
        }

        // Turn on and off each endpoint.
        for (int i = 1; i < count.get() + 1; i++) {
            // On
            Encap<Set> cmd1 = CmdClass.MULTI_CHANNEL.encap(i, CmdClass.BASIC.set(99));
            controller.send(new SendDataMsg<>(nodeId, cmd1)).await();

            // Off
            cmd1 = CmdClass.MULTI_CHANNEL.encap(i, CmdClass.BASIC.set(0));
            controller.send(new SendDataMsg<>(nodeId, cmd1)).await();

            // Report
            ResponsiveEncap<Get, Report> cmd2 = CmdClass.MULTI_CHANNEL.encap(i, CmdClass.BASIC.get());
            controller.send(new SendReceiveDataMsg<>(nodeId, cmd2)).await();
        }

        System.out.println("done\n");
    }

    /**
     * Test add/remove node.
     */
    private static void addRemoveNodeTest() {
        System.out.println("addRemoveNodeTest start");

        System.out.println("  Press a button on a device to add it...");
        // This will automatically timeout after sometime.
        AddNodeTxn txn1 = controller.send(new AddNodeMsg());

        // But we're going to force stop it before the timeout...
        sleep(5000);
        if (!txn1.isComplete()) {
            txn1.stop();
        }
        txn1.await();

        System.out.println("  Press a button on a device to remove it...");
        // This times out too
        RemoveNodeTxn txn2 = controller.send(new RemoveNodeMsg());
        sleep(5000);
        if (!txn2.isComplete()) {
            txn2.stop();
        }
        txn2.await();

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
