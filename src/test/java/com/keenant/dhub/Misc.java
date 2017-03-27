package com.keenant.dhub;

import com.keenant.dhub.zwave.ZServer;
import com.keenant.dhub.zwave.messages.InitDataMsg;
import com.keenant.dhub.zwave.messages.VersionMsg;
import org.junit.Test;

public class Misc {
    @Test
    public void test2() throws InterruptedException {
        ZServer server = new ZServer();
        server.init();
        server.start();

        server.getControllers().forEach(ctrl -> {
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
            ctrl.queue(new VersionMsg());
        });

        while (true) {
            Thread.sleep(10);
        }
    }
}
