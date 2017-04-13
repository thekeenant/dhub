package com.keenant.dhub.hub.plugins.clock;

import com.keenant.dhub.hub.network.Network;
import lombok.ToString;

@ToString
public class ClockNetwork extends Network<ClockDevice> {
    @Override
    public String getUniqueId() {
        return "clock";
    }
}
