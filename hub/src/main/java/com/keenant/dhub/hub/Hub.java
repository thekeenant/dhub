package com.keenant.dhub.hub;

import com.keenant.dhub.core.Lifecycle;
import com.keenant.dhub.hub.plugin.Plugin;
import com.keenant.dhub.hub.web.WebPlugin;
import com.keenant.dhub.hub.zwave.ZPlugin;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.ParseException;

import java.util.*;

public class Hub implements Lifecycle {
    private static Hub instance;

    private Map<Class<? extends Plugin>, Plugin> plugins;
    private Cli<Runnable> cli;

    public static Hub getHub() {
        return instance;
    }

    public Hub() {
        instance = this;
        plugins = new HashMap<>();
        plugins.put(ZPlugin.class, new ZPlugin());
        plugins.put(WebPlugin.class, new WebPlugin());
    }

    public void start() {
        CliBuilder<Runnable> builder = new CliBuilder<>("hub");

        getPlugins().forEach(plugin -> plugin.init(builder));
        getPlugins().forEach(Plugin::start);

        cli = builder.build();
    }

    public void stop() {
        getPlugins().forEach(Plugin::stop);
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
            System.out.println(e.getMessage());
        }
    }
}
