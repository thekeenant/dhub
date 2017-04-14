package com.keenant.dhub.hub.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.keenant.dhub.hub.Bootstrap;
import com.keenant.dhub.hub.Hub;
import io.airlift.airline.Cli.CliBuilder;
import lombok.ToString;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@ToString
public class PluginManager {
    private static final JsonParser parser = new JsonParser();

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

    private void register(Plugin plugin) {
        if (loaded) {
            throw new IllegalStateException("Cannot register plugin after plugins are loaded.");
        }
        plugins.put(plugin.getClass(), plugin);
    }

    public void init() {
        initInternalPlugins();
        initInstalledPlugins();
        Hub.getHub().getLogger().info(plugins.size() + " plugins initialized");
    }

    private void initInternalPlugins() {
        Hub.getHub().getLogger().info("Loading internal plugins");
        Plugin plugin;

        List<InputStream> internals = Arrays.asList(
                getClass().getResourceAsStream("/zplugin.json"),
                getClass().getResourceAsStream("/clockplugin.json")
        );

        // Fancy
        internals.stream().map(this::initPlugin).forEach(this::register);
    }

    private void initInstalledPlugins() {
        Path pluginsFolder = Bootstrap.getWorkingFolder().resolve("plugins");

        if (!pluginsFolder.toFile().exists()) {
            boolean success = pluginsFolder.toFile().mkdir();

            if (!success) {
                Hub.getHub().getLogger().severe("Unable to create plugins directory");
                System.exit(-1);
                return;
            }
            else {
                Hub.getHub().getLogger().info("Created plugins directory");
            }
        }

        Hub.getHub().getLogger().info("Loading installed plugins");

        try {
            Files.newDirectoryStream(pluginsFolder).forEach((path) -> {
                System.out.println(path.toAbsolutePath());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Plugin initPlugin(InputStream pluginConfig) {
        InputStreamReader reader = new InputStreamReader(pluginConfig);

        JsonElement element = parser.parse(reader);

        if (!element.isJsonObject()) {
            throw new RuntimeException("not object");
        }

        JsonObject json = element.getAsJsonObject();

        String main = json.get("main").getAsString();

        Object pluginObject;

        try {
            pluginObject = Class.forName(main).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!Plugin.class.isInstance(pluginObject)) {
            throw new RuntimeException("not plugin");
        }

        Plugin plugin = (Plugin) pluginObject;

        String name = json.get("name").getAsString();
        String version = json.get("version").getAsString();

        PluginConfig config = new PluginConfig(name, version);

        Hub.getHub().getLogger().info("Initializing " + config.getDescriptiveName());
        plugin.init(config);

        return plugin;
    }

    public void load(CliBuilder<Runnable> cli) {
        loaded = true;
        plugins.values().forEach(plugin -> {
            Hub.getHub().getLogger().info("Loading " + plugin.getDescriptiveName());
            plugin.load(cli);
        });

        Hub.getHub().getLogger().info(plugins.size() + " plugins loaded");
    }

    public void enableAll() {
        plugins.values().forEach(Plugin::enable);
    }

    public void disableAll() {
        plugins.values().forEach(Plugin::disable);
    }
}
