package com.keenant.dhub.core.util;

import java.util.Comparator;

public class PrioritizedObject<T> {
    public static final Comparator<PrioritizedObject<?>> ASCENDING = Comparator.comparing(PrioritizedObject::getPriority);
    public static final Comparator<PrioritizedObject<?>> DESCENDING = ASCENDING.reversed();

    private final T object;
    private final Priority priority;

    public PrioritizedObject(T object, Priority priority) {
        this.object = object;
        this.priority = priority;
    }

    public T getObject() {
        return object;
    }

    public Priority getPriority() {
        return priority;
    }
}
