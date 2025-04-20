package com.recalbox.goodset.organizer.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.UncheckedIOException;

@UtilityClass
public class UncheckedIOExceptionThrower {

    @FunctionalInterface
    public interface ThrowingIOExceptionSupplier<T> {
        T get() throws IOException;
    }

    @FunctionalInterface
    public interface ThrowingIOExceptionVoidSupplier {
        void call() throws IOException;
    }

    public static <T> T rethrowIOException(ThrowingIOExceptionSupplier<T> throwingSupplier) {
        try {
            return throwingSupplier.get();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void rethrowIOException(ThrowingIOExceptionVoidSupplier throwingSupplier) {
        try {
            throwingSupplier.call();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
