package com.keenant.dhub.hub.plugin;

import com.keenant.dhub.hub.Hub;
import io.airlift.airline.Cli.CliBuilder;

import java.util.logging.Logger;

public abstract class Plugin {
    private final Logger log = Logger.getLogger(getClass().getSimpleName());

    public Plugin() {
        log.setParent(Hub.getHub().getLogger());
    }

    public Logger getLogger() {
        return log;
    }

    public abstract void load(CliBuilder<Runnable> cli);

    public abstract void enable();

    public abstract void disable();
}
