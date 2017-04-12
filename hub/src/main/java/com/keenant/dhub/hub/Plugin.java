package com.keenant.dhub.hub;

import io.airlift.airline.Cli.CliBuilder;

public abstract class Plugin {
    public abstract void load(CliBuilder<Runnable> cli);

    public abstract void enable();

    public abstract void disable();
}
