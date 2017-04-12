package com.keenant.dhub.hub.network.rules.datetime;

import com.keenant.dhub.hub.network.ProviderRule;

import java.time.ZonedDateTime;

public class DayOfMonthRule implements ProviderRule<ZonedDateTime> {
    private final int startDay;
    private final int endDay;

    public DayOfMonthRule(int startDay, int endDay) {
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public DayOfMonthRule(int day) {
        this(day, day);
    }

    @Override
    public boolean evaluate(ZonedDateTime current) {
        int day = current.getDayOfMonth();
        return day >= startDay && day <= endDay;
    }
}