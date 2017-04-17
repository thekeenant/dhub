package com.keenant.dhub.hub.plugin.clock;

import com.keenant.dhub.hub.network.Network;
import lombok.ToString;

import java.time.ZoneId;

@ToString
public class ClockNetwork extends Network<ClockDevice> {

    @Override
    public void start() {
        super.start();

        addDevice(new ClockDevice(this, ZoneId.systemDefault()));
    }

    @Override
    public String getUniqueId() {
        return "clock";
    }
}
