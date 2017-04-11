package com.keenant.dhub.hub.plugins.zwave;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.core.Lifecycle;
import com.keenant.dhub.hub.network.Data;
import com.keenant.dhub.hub.network.Responsive;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ZPoll implements Lifecycle, Data, Responsive {
    private final Random random = new Random();

    private final Runnable runnable;
    private Timer timer;
    private int interval;

    public ZPoll(Runnable runnable, int interval) {
        this.runnable = runnable;
        this.interval = interval;
    }

    public ZPoll(Runnable runnable) {
        this(runnable, 5000000);
    }

    public void setInterval(int interval) {
        this.interval = interval;
        restart();
    }

    public int getInterval() {
        return interval;
    }

    public void restart() {
        stop();
        start();
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, random.nextInt(interval), interval);
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void respondTo(JsonElement el) {
        JsonObject json = el.getAsJsonObject();
        if (json.has("poll-interval")) {
            int interval = json.get("poll-interval").getAsInt();
            setInterval(interval);
        }
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("poll-interval", interval);
        return json;
    }
}
