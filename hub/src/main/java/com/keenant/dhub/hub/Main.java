package com.keenant.dhub.hub;


import com.keenant.dhub.core.logging.Level;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.network.feature.BinaryFeature;
import com.keenant.dhub.hub.network.feature.ChildrenFeature;
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
                set(hub, line);
//            hub.onCommand(line.split(" "));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while (true) {

        }
    }

    private static void set(Hub hub, String channel) {
        Network network = hub.getNetworks().get(1);

        for (Device device : network.getDevices()) {
            ChildrenFeature<?> feature = device.getFeature(ChildrenFeature.class).orElse(null);

            if (feature == null)
                continue;

            Device child = feature.getDevices().getById(channel).orElse(null);

            if (child == null)
                continue;

            BinaryFeature binary = child.getFeature(BinaryFeature.class).orElse(null);

            if (binary == null)
                continue;

            // Invert current state
            binary.setState(!binary.getState().orElse(true));
        }
    }
}
