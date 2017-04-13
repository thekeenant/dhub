package com.keenant.dhub.hub;

import com.keenant.dhub.hub.network.Network;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException;

import java.util.*;

public class Hub {
    private static Hub instance;

    private PluginManager plugins;
    private NetworkManager networks;
    private ReactionManager reactions;
    private Cli<Runnable> cli;

    public static Hub getHub() {
        return instance;
    }

    public Hub() {
        instance = this;
        plugins = new PluginManager();
        networks = new NetworkManager();
        reactions = new ReactionManager();
    }

    public void registerNetwork(Network provider) {
        networks.add(provider);
    }

    public NetworkManager getNetworkManager() {
        return networks;
    }

    public void start() {
        CliBuilder<Runnable> builder = new CliBuilder<>(" ");
        builder.withCommand(Help.class);

        plugins.load(builder);
        plugins.enableAll();
        cli = builder.build();
    }

    public void stop() {
        plugins.disableAll();
        networks.stopAll();
    }

    public Collection<Plugin> getPlugins() {
        return plugins.values();
    }

    @SuppressWarnings("unchecked")
    public <T extends Plugin> Optional<T> getPlugin(Class<T> plugin) {
        return Optional.ofNullable((T) plugins.get(plugin));
    }

    public boolean hasPlugin(Class<? extends Plugin> plugin) {
        return getPlugin(plugin).isPresent();
    }

    public void onCommand(String[] args) {
        try {
            Runnable runnable = cli.parse(args);
            runnable.run();
        } catch (ParseException e) {
            System.err.println("Command error: " + e.getMessage());
//            e.printStackTrace();
        }
    }
}
