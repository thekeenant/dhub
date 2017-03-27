package com.keenant.dhub.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class Logging {
    private static final ConsoleHandler consoleHandler;

    static {
        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new ConsoleFormatter());
        consoleHandler.setLevel(Level.INFO);
    }

    public static void setLevel(Level level) {
        consoleHandler.setLevel(level);
    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.DEBUG);
        while (logger.getHandlers().length > 0)
            logger.removeHandler(logger.getHandlers()[0]);
        logger.addHandler(consoleHandler);
        return logger;
    }
}
