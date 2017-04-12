package com.keenant.dhub.hub;


import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import jline.console.ConsoleReader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Logging.setLevel(Level.DEV);

        Hub hub = new Hub();
        hub.start();

        ConsoleReader console = new ConsoleReader();
        console.setPrompt("> ");

        String line;
        while ((line = console.readLine()) != null) {
            try {
                hub.onCommand(line.split(" "));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (true) {

        }
    }
}
