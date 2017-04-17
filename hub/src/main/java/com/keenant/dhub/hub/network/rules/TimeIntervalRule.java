package com.keenant.dhub.hub.network.rules;

import com.keenant.dhub.hub.network.ProviderRule;
import com.keenant.dhub.hub.network.provider.DateTimeProvider;

import java.time.ZonedDateTime;

public class TimeIntervalRule extends ProviderRule<DateTimeProvider, ZonedDateTime> {
    private final int interval;

    public TimeIntervalRule(DateTimeProvider provider, int interval) {
        super(provider);
        this.interval = interval;
    }

    @Override
    protected boolean evaluate(ZonedDateTime current) {
        int seconds = current.getHour() * 60 * 60;
        seconds += current.getMinute() * 60;
        seconds += current.getSecond();

        return seconds % interval == 0;
    }
}
