package com.keenant.dhub.hub;

import com.keenant.dhub.core.Lifecycle;
import io.airlift.airline.Cli.CliBuilder;

/**
 * A dhub plugin.
 */
public abstract class Plugin implements Lifecycle {
    private transient Hub hub;

    public abstract void init(CliBuilder<Runnable> cli);

    void setHub(Hub hub) {
        this.hub = hub;
    }

    public Hub getHub() {
        return hub;
    }
}
