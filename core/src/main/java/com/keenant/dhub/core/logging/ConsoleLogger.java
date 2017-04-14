package com.keenant.dhub.core.logging;

import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ConsoleLogger extends Logger {
    private static final ConsoleFormatter formatter = new ConsoleFormatter();

    public ConsoleLogger(String name, ConsoleReader console) {
        super(name, null);
        setLevel(Level.ALL);

//        try {
//            FileHandler fileHandler = new FileHandler(file, 1 << 24, 8, true);
//            fileHandler.setFormatter(formatter);
//            addHandler(fileHandler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                try {
                    CursorBuffer stash = console.getCursorBuffer().copy();
                    console.getOutput().write("\u001b[1G\u001b[K");

                    if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
                        console.print(Ansi.ansi().fg(Color.RED).toString());
                    }
                    else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                        console.print(Ansi.ansi().fg(Color.YELLOW).toString());
                    }
                    else {
                        console.print(Ansi.ansi().fg(Color.WHITE).toString());
                    }

                    console.println(formatter.format(record));
                    console.print(Ansi.ansi().a(Attribute.RESET).toString());
                    console.drawLine();
                    console.flush();

                    console.resetPromptLine("", stash.toString(), stash.cursor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
    }

    @Override
    public void log(LogRecord record) {
        doLog(record);
    }

    public void doLog(LogRecord record) {
        super.log(record);
    }
}
