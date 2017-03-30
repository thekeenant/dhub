package com.keenant.dhub.hub;

import com.keenant.dhub.core.Lifecycle;
import com.keenant.dhub.hub.plugins.Plugin;
import com.keenant.dhub.hub.web.WebPlugin;
import com.keenant.dhub.hub.zwave.ZPlugin;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Hub implements Lifecycle {
    private List<Plugin> plugins;
    private Cli<Runnable> cli;

    public Hub() {
        plugins = new ArrayList<>();
        plugins.add(new ZPlugin());
        plugins.add(new WebPlugin());
    }

    public void start() {
        CliBuilder<Runnable> builder = new CliBuilder<>("hub");

        plugins.forEach(plugin -> plugin.init(builder));
        plugins.forEach(Plugin::start);

        cli = builder.build();
    }

    public void stop() {
        plugins.forEach(Plugin::stop);
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
