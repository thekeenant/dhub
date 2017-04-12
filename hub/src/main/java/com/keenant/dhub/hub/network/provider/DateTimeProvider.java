package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Provider;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

public class DateTimeProvider extends Provider<ZonedDateTime> {
    public DateTimeProvider(Supplier<ZonedDateTime> supplier) {
        super(supplier);
    }

    @Override
    public boolean isEqual(ZonedDateTime before, ZonedDateTime after) {
        return before.toEpochSecond() == after.toEpochSecond();
    }
}
