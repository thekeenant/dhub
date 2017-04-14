package com.keenant.dhub.hub;


import com.keenant.dhub.core.logging.ConsoleLogger;
import com.keenant.dhub.core.logging.ConsoleOutputStream;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class Bootstrap {
    private static ConsoleLogger logger;

    public static Path getWorkingFolder() {
        try {
            Path path = Paths.get(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            if (path.toFile().isFile()) {
                return path.getParent();
            }
            return path;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
            throw new RuntimeException();
        }
    }

    public static ConsoleLogger getLogger() {
        return logger;
    }

    public static void main(String[] args) throws IOException {
        ConsoleReader reader = new ConsoleReader();

        logger = new ConsoleLogger("DHub", reader);
        logger.setLevel(Level.INFO);

        System.setErr(new PrintStream(new ConsoleOutputStream(logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new ConsoleOutputStream(logger, Level.INFO), true));

        Hub hub = new Hub(logger);
        hub.load();

        Thread consoleThread = new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine("")) != null) {
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
