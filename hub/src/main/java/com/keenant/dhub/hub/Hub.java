package com.keenant.dhub.hub;

import com.keenant.dhub.hub.plugin.PluginManager;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Help;
import io.airlift.airline.ParseCommandUnrecognizedException;
import io.airlift.airline.ParseException;
import lombok.ToString;

import java.util.logging.Logger;

@ToString
public class Hub {
    private static Hub instance;

    private PluginManager pluginManager;
    private NetworkManager networkManager;
    private ReactionManager reactionManager;
    private Cli<Runnable> cli;
    private Logger logger;

    public static Hub getHub() {
        return instance;
    }

    public Hub(Logger logger) {
        this.logger = logger;

        instance = this;
        pluginManager = new PluginManager();
        networkManager = new NetworkManager();
        reactionManager = new ReactionManager();
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public ReactionManager getReactionManager() {
        return reactionManager;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public void load() {
        CliBuilder<Runnable> cliBuilder = new CliBuilder<>(" ");
        cliBuilder.withCommand(Help.class);
        pluginManager.load(cliBuilder);
        cli = cliBuilder.build();
    }

    public void start() {
        pluginManager.enableAll();
    }

    public void stop() {
        pluginManager.disableAll();
        networkManager.stopAll();
    }

    public void onCommand(String[] args) {
        try {
            Runnable runnable = cli.parse(args);
            runnable.run();
        } catch (ParseCommandUnrecognizedException e) {
            System.err.println("Unknown command. Type \"help\" for commands.");
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
