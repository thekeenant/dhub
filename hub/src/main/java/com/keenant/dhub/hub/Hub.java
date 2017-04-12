package com.keenant.dhub.hub;

import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.plugins.zwave.ZPlugin;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.ParseException;

import java.util.*;

public class Hub {
    private static Hub instance;

    private Map<Class<? extends Plugin>, Plugin> plugins;
    private List<Network> networks;
    private Cli<Runnable> cli;

    public static Hub getHub() {
        return instance;
    }

    public Hub() {
        instance = this;
        plugins = new HashMap<>();
        plugins.put(ZPlugin.class, new ZPlugin());
        networks = new ArrayList<>();
    }

    public void registerNetwork(Network provider) {
        networks.add(provider);
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public Optional<Network> getNetwork(String id) {
        return networks.stream()
                .filter((network) -> network.getUniqueId().equals(id))
                .findAny();
    }

    public void start() {
        CliBuilder<Runnable> builder = new CliBuilder<>("hub");

        // Initialize plugins
        getPlugins().forEach(plugin -> {
            plugin.load(builder);
        });

        // Start plugins
        getPlugins().forEach(Plugin::enable);

        // Start networks
        getNetworks().forEach(Network::start);

        // Todo:
        // cli = builder.build();
    }

    public void stop() {
        // Stop plugins
        getPlugins().forEach(Plugin::disable);

        // Stop networks
        getNetworks().forEach(Network::stop);
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
            e.printStackTrace();
        }
    }
}
