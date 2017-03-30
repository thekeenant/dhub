package com.keenant.dhub.hub.plugin;

import com.keenant.dhub.core.Lifecycle;
import io.airlift.airline.Cli.CliBuilder;

/**
 * A dhub plugin.
 */
public abstract class Plugin implements Lifecycle {
    public abstract void init(CliBuilder<Runnable> cli);
}
