package com.keenant.dhub.hub.plugins.clock;

import com.keenant.dhub.hub.network.Network;

public class ClockNetwork extends Network<ClockDevice> {
    @Override
    public String getUniqueId() {
        return "clock";
    }
}