package com.recalbox.goodset.organizer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Checker {

    public static void checkArgument(boolean condition, String exceptionMessage, Object... messageArgs) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(exceptionMessage, messageArgs));
        }
    }

}
