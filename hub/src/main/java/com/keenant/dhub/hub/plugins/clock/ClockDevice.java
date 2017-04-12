package com.keenant.dhub.hub.plugins.clock;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.provider.DateTimeProvider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class ClockDevice extends Device {
    private final ZoneId zone;
    private final DateTimeProvider provider;

    private Timer timer;

    public ClockDevice(ClockNetwork network, ZoneId zone) {
        super(network);
        this.zone = zone;
        this.provider = new DateTimeProvider(() -> ZonedDateTime.ofInstant(Instant.now(), zone));
    }

    public void start() {
        addProvider(provider);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                provider.update();
            }
        }, 0, 100);
    }

    public void stop() {
        timer.cancel();
    }
}
