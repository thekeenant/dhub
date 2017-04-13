package com.keenant.dhub.hub;


import com.keenant.dhub.core.logging.ConsoleLogger;
import com.keenant.dhub.core.logging.ConsoleOutputStream;
import com.keenant.dhub.hub.plugins.clock.ClockPlugin;
import com.keenant.dhub.hub.plugins.zwave.ZPlugin;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) throws IOException {
        ConsoleReader reader = new ConsoleReader();

        ConsoleLogger logger = new ConsoleLogger("Hub", "hub.log", reader);
        logger.setLevel(Level.INFO);
        System.setErr(new PrintStream(new ConsoleOutputStream(logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new ConsoleOutputStream(logger, Level.INFO), true));

        Hub hub = new Hub(logger);
        hub.getPluginManager().register(new ClockPlugin());
        hub.getPluginManager().register(new ZPlugin());
        hub.load();

        Thread consoleThread = new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    hub.onCommand(line.split(" "));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread hubThread = new Thread(hub::start);

        consoleThread.start();
        hubThread.start();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }
}
