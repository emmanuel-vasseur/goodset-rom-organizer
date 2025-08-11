package com.recalbox.goodset.organizer.main.init;

import lombok.experimental.UtilityClass;

import java.util.logging.LogManager;

import static com.recalbox.goodset.organizer.util.UncheckedIOExceptionThrower.rethrowIOException;

@UtilityClass
public class CommandLineRunnerInitializer {

    private static final String DEFAULT_ROM_DIRECTORY = ".";

    static {
        configureJavaUtilLogging();
    }

    public static RomOrganizer createRomOrganizer(String... commandLineArguments) {
        String romDirectory = commandLineArguments.length > 0 ? commandLineArguments[0] : DEFAULT_ROM_DIRECTORY;
        return new RomOrganizer(romDirectory);
    }

    private static void configureJavaUtilLogging() {
        rethrowIOException(() -> LogManager.getLogManager().readConfiguration(
                CommandLineRunnerInitializer.class.getClassLoader().getResourceAsStream("logging.properties")));
    }
}
