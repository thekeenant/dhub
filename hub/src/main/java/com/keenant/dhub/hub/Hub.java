package com.keenant.dhub.hub;

import com.keenant.dhub.hub.zwave.ZServer;

public class Hub {
    private ZServer zserver;

    public Hub() {
        zserver = new ZServer();
    }

    public ZServer getZServer() {
        return zserver;
    }

    public void start() {
        zserver.start();
    }

    public void stop() {
        zserver.stop();
    }
}
