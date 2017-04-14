package com.keenant.dhub.hub.plugin;

public class PluginConfig {
    private final String name;
    private final String version;

    public PluginConfig(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescriptiveName() {
        return name + " (" + version + ")";
    }
}
