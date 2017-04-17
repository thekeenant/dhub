package com.keenant.dhub.hub.network.rules;

import com.keenant.dhub.hub.network.ProviderRule;
import com.keenant.dhub.hub.network.provider.DateTimeProvider;

import java.time.ZonedDateTime;

public class TimeOfDayRule extends ProviderRule<DateTimeProvider<?>, ZonedDateTime> {
    private static final long MAX_VALUE = 24 * 60 * 60 - 1;

    private final long startSeconds;
    private final long endSeconds;

    public TimeOfDayRule(DateTimeProvider provider, long startSeconds, long endSeconds) {
        super(provider);
        if (startSeconds < 0 || startSeconds < 0 || startSeconds > MAX_VALUE || endSeconds > MAX_VALUE) {
            throw new IllegalArgumentException("Time must be between hour 0 and hour 23, minute 59, second 59.");
        }
        this.startSeconds = startSeconds;
        this.endSeconds = endSeconds;
    }

    public TimeOfDayRule(DateTimeProvider provider, int startHour, int startMinute, int startSecond, int endHour, int endMinute, int endSecond) {
        this(provider,
                startHour * 60 * 60 + startMinute * 60 + startSecond,
                endHour * 60 * 60 + endMinute * 60 + endSecond);
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