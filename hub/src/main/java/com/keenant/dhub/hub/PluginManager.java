package com.keenant.dhub.hub;

import io.airlift.airline.Cli.CliBuilder;

import java.util.HashMap;
import java.util.Map;

public class PluginManager {
    private final Map<Class<? extends Plugin>, Plugin> plugins = new HashMap<>();
    private boolean loaded;

    public void register(Plugin plugin) {
        if (loaded) {
            throw new IllegalStateException("Cannot register plugin after plugins are loaded.");
        }
        plugins.put(plugin.getClass(), plugin);
    }

    public void load(CliBuilder<Runnable> cli) {
        loaded = true;
        plugins.values().forEach(plugin -> plugin.load(cli));
    }

    public void enableAll() {
        plugins.values().forEach(Plugin::enable);
    }

    public void disableAll() {
        plugins.values().forEach(Plugin::disable);
    }
}
