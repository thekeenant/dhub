package com.keenant.dhub.hub.plugin;

import io.airlift.airline.Cli.CliBuilder;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ToString
public class PluginManager {
    private final Map<Class<? extends Plugin>, Plugin> plugins = new HashMap<>();
    private boolean loaded;

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
