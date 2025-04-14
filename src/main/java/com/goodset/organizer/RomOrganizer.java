package com.goodset.organizer;

import com.goodset.organizer.rom.RomNameHandling;
import com.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Log
@RequiredArgsConstructor
public class RomOrganizer {

    private static final String SUB_DIRECTORY_FOR_LOWER_QUALITY_ROMS = "Other versions";

    private final RomNameHandling romNameHandling = new RomNameHandling();
    private final File romDirectory;

    public static RomOrganizer init(String... commandLineArguments) {
        String romDirectory = commandLineArguments.length > 0 ? commandLineArguments[0] : ".";
        return new RomOrganizer(new File(romDirectory));
    }

    public void listUnknownRomTypes() {
        List<File> romFiles = FileUtils.listFiles(romDirectory);
        List<String> unknownRomTypes = romFiles.stream()
                .map(File::getName)
                .filter(romNameHandling::hasUnknownRomTypes)
                .collect(Collectors.toList());

        if (unknownRomTypes.isEmpty()) {
            log.info(() -> "No unknown rom types found in '" + romDirectory + "'");
        } else {
            log.info(() -> "Unknown rom types found in '" + romDirectory + "':");
            unknownRomTypes.forEach(log::info);
        }
    }

    public void renameRomTypes() {
        List<File> romFiles = FileUtils.listFiles(romDirectory);
        romFiles.forEach(this::renameRom);
    }

    private void renameRom(File rom) {
        String newName = romNameHandling.replaceRomTypes(rom.getName());
        FileUtils.renameFileName(rom, newName);
    }

    public void groupRomsByGame() {
        List<File> romFiles = FileUtils.listFiles(romDirectory);
        romFiles.forEach(this::moveRomToGameDirectory);

        List<File> gameDirectories = FileUtils.listDirectories(romDirectory);
        gameDirectories.forEach(this::splitGoodAndNotGoodRoms);
    }

    private void splitGoodAndNotGoodRoms(File gameDirectory) {
        List<String> gameRomNames = FileUtils.listFiles(gameDirectory).stream()
                .map(File::getName)
                .collect(Collectors.toList());
        romNameHandling.getLowerQualityRomNames(gameRomNames).forEach(romName ->
                FileUtils.moveToSubDirectory(new File(gameDirectory, romName), SUB_DIRECTORY_FOR_LOWER_QUALITY_ROMS));
    }

    private void moveRomToGameDirectory(File rom) {
        String game = romNameHandling.getGame(rom.getName());
        FileUtils.moveToSubDirectory(rom, game);
    }

}
