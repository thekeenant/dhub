package com.keenant.dhub;

import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.ZNode;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import org.junit.Test;

public class Misc {
    @Test
    public void misc() throws InterruptedException {

        Hub hub = new Hub();
        hub.start();

        ZController controller = hub.getZServer().getControllers().get(0);

        ZNode node = new ZNode(controller, (byte) 14);

        node.sendCmd(BasicCmd.set(42));
        node.sendCmd(BasicCmd.get());

        while (true) {

        }
    }
}
