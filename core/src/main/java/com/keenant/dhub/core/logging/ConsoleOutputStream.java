package com.keenant.dhub.core.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleOutputStream extends ByteArrayOutputStream {
    private final Logger logger;
    private final Level level;

    public ConsoleOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void flush() throws IOException {
        String contents = toString(Charset.defaultCharset().name());
        super.reset();

        if (!contents.isEmpty() && !contents.equals(System.lineSeparator())) {
            logger.logp(level, "", "", contents);
        }
    }
}