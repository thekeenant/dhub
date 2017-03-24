package com.keenant.dhub.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConsoleFormatter extends Formatter {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        String format = "%1$s %2$s %3$10s: %4$s\n";
        String date = TIME_FORMAT.format(new Date(record.getMillis()));
        String name = record.getLoggerName();
        String level = "[" + record.getLevel().getName() + "]";
        String message = record.getMessage();
        return String.format(format, date, name, level, message);
    }
}
