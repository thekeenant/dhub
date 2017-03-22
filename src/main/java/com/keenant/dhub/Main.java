package com.keenant.dhub;

import jline.console.ConsoleReader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DHub hub = new DHub();
        hub.start();

        ConsoleReader console = new ConsoleReader();
        console.setPrompt("> ");

        String line = null;
        while ((line = console.readLine()) != null) {
            // ...
        }
    }
}
