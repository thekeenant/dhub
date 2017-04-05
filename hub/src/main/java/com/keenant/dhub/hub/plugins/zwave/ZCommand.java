package com.keenant.dhub.hub.plugins.zwave;

import io.airlift.airline.Command;

@Command(name = "info")
public class ZCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Ran a command!");
    }

    @Command(name = "send")
    public static class SendDataCmd implements Runnable {

        @Override
        public void run() {

        }
    }
}
