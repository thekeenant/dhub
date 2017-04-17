package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Provider;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Provides a zoned datetime, with precision to seconds.
 */
@ToString(callSuper = true)
public class DateTimeProvider<D extends Device> extends Provider<D, ZonedDateTime> {
    private final ZoneId zone;

    public DateTimeProvider(D device, ZoneId zone) {
        super(device);
        this.zone = zone;
    }

    @Override
    public Optional<ZonedDateTime> fetch() {
        return Optional.of(ZonedDateTime.now(zone));
    }

    @Override
    public boolean isEqual(ZonedDateTime before, ZonedDateTime after) {
        return before.toEpochSecond() == after.toEpochSecond();
    }
}
