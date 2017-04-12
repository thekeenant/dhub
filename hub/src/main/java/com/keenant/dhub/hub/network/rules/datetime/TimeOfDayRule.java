package com.keenant.dhub.hub.network.rules.datetime;

import com.keenant.dhub.hub.network.ProviderRule;

import java.time.ZonedDateTime;

public class TimeOfDayRule implements ProviderRule<ZonedDateTime> {
    private final long startSeconds;
    private final long endSeconds;

    public TimeOfDayRule(long startSeconds, long endSeconds) {
        this.startSeconds = startSeconds;
        this.endSeconds = endSeconds;
    }

    @Override
    public boolean evaluate(ZonedDateTime current) {
        int hours = current.getHour();
        int minutes = current.getMinute();
        int seconds = current.getSecond();

        int totalSeconds = hours * 60 * 60;
        totalSeconds += minutes * 60;
        totalSeconds += seconds;

        return totalSeconds >= startSeconds && totalSeconds <= endSeconds;
    }
}