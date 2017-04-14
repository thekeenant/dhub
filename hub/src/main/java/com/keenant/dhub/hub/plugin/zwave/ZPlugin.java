package com.keenant.dhub.hub.plugin.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.plugin.Plugin;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;

import java.util.*;
import java.util.logging.Logger;

public class ZPlugin extends Plugin {
    private static final int MAX_RETRIES = 5;
    private static ZPlugin instance;

    private final Map<String, Integer> attempts = new HashMap<>();
    private final Timer serialPortUpdater = new Timer();

    @Override
    public void load(CliBuilder<Runnable> cli) {
        cli.withGroup("zplugin")
                .withDescription("ZPlugin commands")
                .withDefaultCommand(RefreshCommand.class)
                .withCommand(RefreshCommand.class);
    }

    @Command(name = "refresh", description = "Refresh serial Z-Wave serial ports")
    public static class RefreshCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("Refreshing serial ports...");
            instance.attempts.clear();
        }
    }

    @Override
    public void enable() {
        instance = this;

        serialPortUpdater.schedule(new TimerTask() {
            @Override
            public void run() {
                updateSerialPorts();
            }
        }, 0, 1000);
    }

    @Override
    public void disable() {
        serialPortUpdater.cancel();
    }

    private void updateSerialPorts() {
        List<SerialPort> ports = Arrays.asList(SerialPort.getCommPorts());
        ports.sort(Comparator.comparing(SerialPort::getSystemPortName));

        List<String> existing = new ArrayList<>();
        for (Network network : Hub.getHub().getNetworkManager().getNetworks()) {
            if (network instanceof ZNetwork) {
                existing.add(((ZNetwork) network).getPort().getSystemPortName());
            }
        }

        for (SerialPort port : ports) {
            if (attempts.containsKey(port.getSystemPortName())) {
                continue;
            }

            if (existing.contains(port.getSystemPortName())) {
                continue;
            }

            getLogger().info("Serial port detected at " +  port.getSystemPortName() + " (" + port.getDescriptivePortName() + ")");

            Logger logger = createLogger(port.getSystemPortName());
            Hub.getHub().getNetworkManager().register(new ZNetwork(this, port, logger));
        }
    }

    public void retry(SerialPort name, ZNetwork network) {
        int num = attempts.getOrDefault(name.getSystemPortName(), 1);

        if (num > MAX_RETRIES) {
            getLogger().warning("Disabling failed network after " + MAX_RETRIES + " retries: " + network.getUniqueId());
            Hub.getHub().getNetworkManager().unregister(network);
            return;
        }

        getLogger().warning("Retrying network in 1 second (" + num + "): " + network.getUniqueId());
        attempts.put(name.getSystemPortName(), num + 1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        network.stop();
        network.start();
    }
}
