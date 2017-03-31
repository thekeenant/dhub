package com.keenant.dhub.hub;


import jline.console.ConsoleReader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Hub hub = new Hub();
        hub.start();

        ConsoleReader console = new ConsoleReader();
        console.setPrompt("> ");

        String line;
        while ((line = console.readLine()) != null) {
            hub.onCommand(line.split(" "));
        }

        while (true) {

        }
    }
}
