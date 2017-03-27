package com.keenant.dhub;

import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.ZNode;
import com.keenant.dhub.zwave.ZServer;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.messages.VersionMsg;
import org.junit.Test;

public class Misc {
    @Test
    public void test2() throws InterruptedException {
        ZServer server = new ZServer();
        server.init();
        server.start();

        ZController control = server.getByName("ttyACM0").orElse(null);

        ZNode switches = new ZNode(control, 34);
        ZNode bulb = new ZNode(control, 35);

        System.out.println();

        switches.queueCmd(SwitchBinaryCmd.set(false));
        switches.queueCmd(SwitchBinaryCmd.set(true));
        switches.queueCmd(SwitchBinaryCmd.set(false));

//        bulb.queueCmd(BasicCmd.set(99));
        bulb.queueCmd(SwitchBinaryCmd.set(false));

        while (true) {
            Thread.sleep(100);
        }
    }
}
