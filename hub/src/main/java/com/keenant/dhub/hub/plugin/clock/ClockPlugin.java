package com.keenant.dhub.hub.plugin.clock;

import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.plugin.Plugin;
import io.airlift.airline.Arguments;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.ParseException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClockPlugin extends Plugin {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    @Override
    public void load(CliBuilder<Runnable> cli) {
        cli.withCommand(ClockCommand.class);
    }

    @Override
    public void enable() {
        Hub.getHub().getNetworkManager().register(new ClockNetwork());
    }

    @Override
    public void disable() {

    }

    @Command(name = "clock", description = "Current time based on system time or a time zone")
    public static class ClockCommand implements Runnable {
        @Arguments(usage = "<time zone>")
        private List<String> args;

        @Override
        public void run() {
            ZoneId zone = ZoneId.systemDefault();

            if (args != null) {
                try {
                    zone = ZoneId.of(args.get(0));
                } catch (Exception e) {
                    throw new ParseException(e.getMessage());
                }
            }

            ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), zone);
            System.out.println("Time in " + zone + ": " + time.format(format));
        }
    }
}
