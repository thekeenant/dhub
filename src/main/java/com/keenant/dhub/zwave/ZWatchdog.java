package com.keenant.dhub.zwave;

import com.keenant.dhub.logging.Logging;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Search for more controllers and disable dead controllers for the ZServer.
 */
public class ZWatchdog extends Thread {
    private final Logger log;
    private final ZServer server;

    public ZWatchdog(ZServer server) {
        this.log = Logging.getLogger("ZWatchdog");
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            // Todo: Scan for more?

            for (ZController controller : server.getControllers()) {
                if (!controller.isAlive()) {
                    // Todo: Try to revive?
                    continue;
                }

                Transaction curr = controller.updateCurrent().orElse(null);

                if (curr == null) {
                    continue;
                }

                long ms = TimeUnit.MILLISECONDS.convert(curr.getNanosAlive(), TimeUnit.NANOSECONDS);

                if (ms > 10000) {
                    log.severe("Disabling unresponsive controller: " + controller);
                    controller.stop();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
