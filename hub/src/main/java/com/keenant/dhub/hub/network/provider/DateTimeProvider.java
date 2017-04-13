package com.keenant.dhub.hub.network.provider;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Provider;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * Provides a zoned datetime, with precision to seconds.
 */
@ToString(callSuper = true)
public class DateTimeProvider extends Provider<ZonedDateTime> {
    public DateTimeProvider(Device device, Supplier<ZonedDateTime> supplier) {
        super(device, supplier, supplier.get());
    }

    @Override
    public boolean isEqual(ZonedDateTime before, ZonedDateTime after) {
        return before.toEpochSecond() == after.toEpochSecond();
    }
}
