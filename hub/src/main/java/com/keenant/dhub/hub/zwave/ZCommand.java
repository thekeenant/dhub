package com.keenant.dhub.hub.zwave;

import io.airlift.airline.Command;

@Command(name = "info")
public class ZCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Ran a command!");
    }

    @Command(name = "send")
    public static class SendData implements Runnable {

        @Override
        public void run() {

        }
    }
}