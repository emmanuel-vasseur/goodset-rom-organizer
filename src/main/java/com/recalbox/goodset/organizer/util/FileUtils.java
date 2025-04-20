package com.recalbox.goodset.organizer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.recalbox.goodset.organizer.util.UncheckedIOExceptionThrower.rethrowIOException;
import static java.nio.charset.StandardCharsets.UTF_8;

@Log
@UtilityClass
public class FileUtils {

    public static List<Path> listFiles(Path directory) {
        return listDirectoryContent(directory, Files::isRegularFile);
    }

    public static List<Path> listDirectories(Path directory) {
        return listDirectoryContent(directory, Files::isDirectory);
    }

    private static List<Path> listDirectoryContent(Path directory, Predicate<Path> directoryFilter) {
        return rethrowIOException(() -> Files.list(directory))
                .filter(directoryFilter)
                .collect(Collectors.toList());
    }

    public static void renameFileName(Path rom, String newName) {
        Path newRom = rom.resolveSibling(newName);
        moveFile(rom, newRom);
    }

    public static void moveToSubDirectory(Path file, String subDirectory) {
        Path targetDirectory = file.resolveSibling(subDirectory);
        createDirectories(targetDirectory);
        moveFile(file, targetDirectory.resolve(file.getFileName()));
    }

    public static void moveFile(Path rom, Path newRom) {
        if (Files.exists(newRom)) {
            log.severe(String.format("Could not move file '%s' to '%s', file exists", rom, newRom));
            return;
        }

        rethrowIOException(() -> Files.move(rom, newRom));
    }

    public static void createDirectories(Path directory) {
        rethrowIOException(() -> Files.createDirectories(directory));
    }

    public static Stream<String> readLines(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, UTF_8))
                .lines();
    }

    public static List<String> readAllLines(Path file) {
        return rethrowIOException(() -> Files.readAllLines(file, UTF_8));
    }

    public static void deleteFileAndAllEmptyParentDirectories(Path file) {
        delete(file);
        Path parentDirectory = file.getParent();
        while (isEmpty(parentDirectory)) {
            delete(parentDirectory);
            parentDirectory = parentDirectory.getParent();
        }
    }

    public static void delete(Path fileOrDirectory) {
        rethrowIOException(() -> Files.delete(fileOrDirectory));
    }

    public static boolean isEmpty(Path fileOrDirectory) {
        return !rethrowIOException(() -> Files.list(fileOrDirectory))
                .findAny().isPresent();
    }

    public static void writeLinesIntoFile(List<String> lines, Path file) {
        rethrowIOException(() -> Files.write(file, lines, UTF_8));
    }
}

