package com.keenant.dhub;

import com.keenant.dhub.zwave.ZServer;

public class DHub {
    private ZServer server;

    public DHub() {
        server = new ZServer();
    }

    public void start() {
        server.start();

    }
}
