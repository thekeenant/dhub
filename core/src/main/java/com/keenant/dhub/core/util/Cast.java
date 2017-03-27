package com.keenant.dhub.core.util;

public class Cast {
    @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
    public static <F, T> T to(F from, Class<T> toClass) {
        if (from == null) {
            return null;
        }
        else {
            return (T) from;
        }
    }

    public static <F> Integer toInt(F from) {
        if (from == null) {
            return null;
        }
        else {
            return to(from, Integer.class);
        }
    }
//
//    @SuppressWarnings("unchecked")
//    protected <S, T extends S> Optional<T> to(Optional<S> opt, Class<T> desiredSubclass) {
//        if (desiredSubclass.isInstance(opt))
//            return Optional.of((T) opt);
//        else
//            return Optional.absent();
//    }
}
