package com.keenant.dhub.hub.plugin;

import com.keenant.dhub.hub.Hub;
import io.airlift.airline.Cli.CliBuilder;

import java.util.logging.Logger;

public abstract class Plugin {
    private PluginConfig pluginConfig;
    private Logger log;

    public final void init(PluginConfig config) {
        this.pluginConfig = config;
        log = Logger.getLogger(getName());
        log.setParent(Hub.getHub().getLogger());
    }

    public abstract void load(CliBuilder<Runnable> cli);

    public abstract void enable();

    public abstract void disable();

    public String getName() {
        return pluginConfig.getName();
    }

    public String getVersion() {
        return pluginConfig.getVersion();
    }

    public String getDescriptiveName() {
        return pluginConfig.getDescriptiveName();
    }

    public Logger getLogger() {
        return log;
    }

    public Logger createLogger(String name) {
        return new PluginLogger(this, name);
    }
}
