package com.recalbox.goodset.organizer.main;

import com.recalbox.goodset.organizer.RomOrganizer;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.logging.LogManager;

@UtilityClass
public class CommandLineRunnerInitialization {

    private static final String DEFAULT_ROM_DIRECTORY = ".";

    public static RomOrganizer createRomOrganizer(String... commandLineArguments) {
        configureJavaUtilLogging();
        String romDirectory = commandLineArguments.length > 0 ? commandLineArguments[0] : DEFAULT_ROM_DIRECTORY;
        return new RomOrganizer(new File(romDirectory));
    }

    private static void configureJavaUtilLogging() {
        try (InputStream is = CommandLineRunnerInitialization.class.getClassLoader().
                getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
