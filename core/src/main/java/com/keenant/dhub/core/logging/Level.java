package com.keenant.dhub.core.logging;

public class Level extends java.util.logging.Level {
    public static final Level DEBUG = new Level("DEBUG", 1);
    public static final Level DEV = new Level("DEV", 100);
    public static final Level FINE = new Level("FINE", java.util.logging.Level.FINE.intValue());
    public static final Level INFO = new Level("INFO", java.util.logging.Level.INFO.intValue());
    public static final Level WARNING = new Level("WARNING", java.util.logging.Level.WARNING.intValue());
    public static final Level SEVERE = new Level("SEVERE", java.util.logging.Level.SEVERE.intValue());

    private Level(String name, int value) {
        super(name, value);
    }
}
