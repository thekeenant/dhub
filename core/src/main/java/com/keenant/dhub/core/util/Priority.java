package com.keenant.dhub.core.util;

public class Priority implements Comparable<Priority> {
    public static final Priority HIGHEST = of(100);
    public static final Priority HIGH = of(75);
    public static final Priority DEFAULT = of(50);
    public static final Priority LOW = of(25);
    public static final Priority LOWEST = of(0);

    public static Priority of(int value) {
        return new Priority(value);
    }

    private int value;

    private Priority(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Priority must be between 0 and 100, inclusive.");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Priority o) {
        return Integer.compare(value, o.value);
    }
}
