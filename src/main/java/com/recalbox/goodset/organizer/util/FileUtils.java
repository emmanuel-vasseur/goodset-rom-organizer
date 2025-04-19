package com.recalbox.goodset.organizer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

@Log
@UtilityClass
public class FileUtils {

    public static List<File> listFiles(File directory) {
        return listContent(directory, File::isFile);
    }

    public static List<File> listDirectories(File directory) {
        return listContent(directory, File::isDirectory);
    }

    private static List<File> listContent(File directory, FileFilter fileFilter) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Directory '" + directory + "' does not exits");
        }

        File[] files = directory.listFiles(fileFilter);
        return Arrays.asList(requireNonNull(files));
    }

    public static void renameFileName(File rom, String newName) {
        File newRom = new File(rom.getParentFile(), newName);
        moveFile(rom, newRom);
    }

    public static void moveToSubDirectory(File file, String subDirectory) {
        File targetDirectory = new File(file.getParentFile(), subDirectory);
        createDirectory(targetDirectory);
        moveFile(file, new File(targetDirectory, file.getName()));
    }

    public static void moveFile(File rom, File newRom) {
        if (newRom.exists()) {
            log.severe(() -> "Could not move file '" + rom + "' to '" + newRom + "', file exists");
            return;
        }

        boolean renameSucceed = rom.renameTo(newRom);
        if (!renameSucceed) {
            log.severe(() -> "Could not move file '" + rom + "' to '" + newRom + "'");
        }
    }

    public static void createDirectory(File gameDirectory) {
        boolean directoryCreatedOrAlreadyExists = gameDirectory.mkdir() || gameDirectory.isDirectory();
        if (!directoryCreatedOrAlreadyExists) {
            log.severe(() -> "Could not create directory '" + gameDirectory + "'");
        }
    }

    public static Stream<String> readLines(InputStream romTypeMappingsInputStream) {
        return new BufferedReader(new InputStreamReader(romTypeMappingsInputStream, UTF_8))
                .lines()
                .filter(line -> !line.isEmpty());
    }
}
