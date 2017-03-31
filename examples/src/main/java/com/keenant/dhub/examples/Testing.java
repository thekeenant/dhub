package com.keenant.dhub.examples;

import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.event.cmd.BasicReportEvent;
import com.keenant.dhub.zwave.messages.*;

public class Testing {
    public static void main(String[] args) throws InterruptedException {
        Logging.setLevel(Level.INFO);

        Controller controller = new Controller("ttyACM0");
        controller.start();

        controller.listen(TransactionCompleteEvent.class, (listener, event) -> {
            System.out.println(event.getTransaction().millisAlive());
        });

        controller.listen(BasicReportEvent.class, (listener, event) -> {
            int node = event.getNodeId();
            int level = event.getCmd().getValue();

            System.out.println("Node #" + node + " = " + level + "%");
        });

        controller.send(new RequestNodeInfoMsg(43));
        controller.send(new RequestNodeInfoMsg(43));

        controller.send(new SendDataMsg(43, CmdClass.BASIC.get()));
        controller.send(new SendDataMsg(43, CmdClass.BASIC.get()));
        controller.send(new SendDataMsg(43, CmdClass.BASIC.get()));
    }
}
