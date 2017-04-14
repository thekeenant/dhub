package com.keenant.dhub.hub.plugin.clock;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.provider.DateTimeProvider;
import lombok.ToString;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

@ToString
public class ClockDevice extends Device<ClockNetwork> {
    private final ZoneId zone;
    private final DateTimeProvider provider;

    private Timer timer;

    public ClockDevice(ClockNetwork network, ZoneId zone) {
        super(network);
        this.zone = zone;
        this.provider = new DateTimeProvider(this, () -> ZonedDateTime.ofInstant(Instant.now(), zone));
    }

    @Override
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

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public ZoneId getZone() {
        return zone;
    }
}
