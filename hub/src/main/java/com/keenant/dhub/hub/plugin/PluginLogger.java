package com.keenant.dhub.hub.plugin;

import com.keenant.dhub.hub.Bootstrap;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger extends Logger {
    private final String name;

    public PluginLogger(Plugin plugin, String name) {
        super(plugin.getName(), null);
        this.name = name;
        setParent(Bootstrap.getLogger());
    }

    public PluginLogger(Plugin plugin) {
        this(plugin, null);
    }

    @Override
    public void log(LogRecord record)
    {
        if (name != null) {
            record.setMessage("[" + name + "] " + record.getMessage());
        }
        super.log(record);
    }
}
