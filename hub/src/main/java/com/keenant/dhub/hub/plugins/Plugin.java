package com.keenant.dhub.hub.plugins;

/**
 * A dhub plugin.
 */
public abstract class Plugin {
    public abstract void load();

    public abstract void unload();
}
