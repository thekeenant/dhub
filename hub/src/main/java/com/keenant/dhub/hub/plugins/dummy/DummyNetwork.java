package com.keenant.dhub.hub.plugins.dummy;

import com.keenant.dhub.hub.network.Network;

public class DummyNetwork extends Network<DummyDevice> {
    @Override
    public String getUniqueId() {
        return "dummy";
    }
}
