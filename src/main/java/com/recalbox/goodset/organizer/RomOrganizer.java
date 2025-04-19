package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;

@Log
@RequiredArgsConstructor
public class RomOrganizer {

    private static final String SUB_DIRECTORY_FOR_LOWER_QUALITY_ROMS = "Other versions";
    public static final String GAMELIST_FILENAME = "gamelist.xml";

    private final RomNameHandling romNameHandling = new RomNameHandling();
    private final File romDirectory;

    public void listUnknownRomTypes() {
        List<File> romFiles = FileUtils.listFiles(romDirectory);
        List<SimpleEntry<String, List<String>>> romNamesWithUnknownTypes = romFiles.stream()
                .map(rom -> new SimpleEntry<>(rom.getName(), romNameHandling.hasUnknownRomTypes(rom.getName())))
                .filter(romAttributes -> !romAttributes.getValue().isEmpty())
                .collect(Collectors.toList());

        if (romNamesWithUnknownTypes.isEmpty()) {
            log.info(() -> "** Result OK, No unknown rom types found in '" + romDirectory + "' files **");
        } else {
            log.warning(() -> "******** Unknown rom types found in '" + romDirectory + "' files ********");
            romNamesWithUnknownTypes.forEach(romAttributes -> log.warning(
                    romAttributes.getKey() + " : " + String.join(", ", romAttributes.getValue())
            ));
        }
    }

    public void renameRomTypes() {
        List<File> romFiles = FileUtils.listFiles(romDirectory);
        romFiles.forEach(this::renameRom);
    }

    private void renameRom(File rom) {
        String newName = romNameHandling.replaceRomTypes(rom.getName());
        if (!rom.getName().equals(newName)) {
            FileUtils.renameFileName(rom, newName);
        }
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

    List<String> loadGameList() {
        try (FileInputStream gameListInputStream = new FileInputStream(new File(romDirectory, GAMELIST_FILENAME))) {
            return FileUtils.readLines(gameListInputStream).collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
