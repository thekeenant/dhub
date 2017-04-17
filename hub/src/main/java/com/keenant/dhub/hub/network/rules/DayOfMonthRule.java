package com.keenant.dhub.hub.network.rules;

import com.keenant.dhub.hub.network.ProviderRule;
import com.keenant.dhub.hub.network.provider.DateTimeProvider;

import java.time.ZonedDateTime;

public class DayOfMonthRule extends ProviderRule<DateTimeProvider, ZonedDateTime> {
    private final int startDay;
    private final int endDay;

    public DayOfMonthRule(DateTimeProvider provider, int startDay, int endDay) {
        super(provider);
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public DayOfMonthRule(DateTimeProvider provider, int day) {
        this(provider, day, day);
    }

    @Override
    public boolean evaluate(ZonedDateTime current) {
        int day = current.getDayOfMonth();
        return day >= startDay && day <= endDay;
    }
}