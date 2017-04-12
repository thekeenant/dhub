package com.keenant.dhub.hub.plugins.clock;

import com.keenant.dhub.hub.network.Device;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class ClockDevice extends Device {
    private final ZoneId zone;
    private Timer timer;
    private ZonedDateTime latestTime;

    public ClockDevice(ClockNetwork network, ZoneId zone) {
        super(network);
        this.zone = zone;
    }

    public static void main(String[] args) {
        ClockDevice d = new ClockDevice(null, ZoneId.systemDefault());
        d.start();
    }

    private void updateTime() {
        ZonedDateTime updatedTime = ZonedDateTime.ofInstant(Instant.now(), zone);

        if (latestTime != null) {
            boolean newSecond = latestTime.toEpochSecond() != updatedTime.toEpochSecond();

            if (newSecond) {
                // Todo:
                System.out.println("New second: " + updatedTime);
            }
        }

        latestTime = updatedTime;
    }

    public void start() {
        updateTime();

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 100, 100);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
