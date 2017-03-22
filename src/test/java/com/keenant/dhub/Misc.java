package com.keenant.dhub;

import com.keenant.dhub.zwave.ZStick;
import com.keenant.dhub.zwave.cmd.BasicSetCmd;
import com.keenant.dhub.zwave.frame.SendDataFrame;
import org.junit.Test;

public class Misc {
    @Test
    public void misc() throws InterruptedException {
        DHub hub = new DHub();
        hub.start();

        Thread.sleep(1000);

        ZStick stick = hub.getServer().getControllers().get(0).getStick();

        for (int i = 0; i < 100; i++) {
            BasicSetCmd cmd = new BasicSetCmd(i % 2 == 0 ? 0 : 99);
            SendDataFrame send = new SendDataFrame(11, cmd);
            stick.write(send);
            Thread.sleep(2000);
        }


        while (true) {

        }
    }
}
