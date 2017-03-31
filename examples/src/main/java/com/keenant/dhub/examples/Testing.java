package com.keenant.dhub.examples;

import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.messages.*;
import com.keenant.dhub.zwave.transaction.RemoveNodeTransaction;

public class Testing {
    public static void main(String[] args) throws InterruptedException {
        Logging.setLevel(Level.DEV);

        Controller controller = new Controller("ttyACM0");
        controller.start();

        Thread.sleep(2000);
        System.out.println("Sending");


        controller.send(RequestNodeInfoMsg.get(43));
        controller.send(RequestNodeInfoMsg.get(43));

//        Thread.sleep(3000);
//        controller.send(SendDataMsg.of(37, BasicCmd.set(0)));
//        controller.send(SendDataMsg.of(37, BasicCmd.setPercent(100.0)));
//        controller.send(SendDataMsg.of(37, BasicCmd.set(0)));
    }
}
