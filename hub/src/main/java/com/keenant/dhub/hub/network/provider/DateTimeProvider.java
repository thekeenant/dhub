package com.keenant.dhub.hub.network.provider;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.Provider;
import com.keenant.dhub.hub.util.Transformer;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Provides a zoned datetime, with precision to seconds.
 */
@ToString(callSuper = true)
public class DateTimeProvider extends Provider<Device, ZonedDateTime> {
    private final ZoneId zone;

    public DateTimeProvider(Device device, ZoneId zone) {
        super(device);
        this.zone = zone;
    }

    @Override
    public String getUniqueId() {
        return "datetime";
    }

    @Override
    public String getDataType() {
        return "datetime";
    }

    @Override
    public Optional<ZonedDateTime> fetch() {
        return Optional.of(ZonedDateTime.now(zone));
    }

    @Override
    public boolean isEqual(ZonedDateTime before, ZonedDateTime after) {
        return before.toEpochSecond() == after.toEpochSecond();
    }

    @Override
    protected Transformer<Optional<ZonedDateTime>, JsonElement> toJson() {
        return (opt) -> opt.isPresent() ? new JsonPrimitive(opt.orElse(null) + "") : JsonNull.INSTANCE;
    }
}
