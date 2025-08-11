package com.recalbox.goodset.organizer.main.deprecated;

import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;

@RequiredArgsConstructor
public class GroupRomsByGame {

    private final RomOrganizer romOrganizer;

    public static void main(String... args) {
        new GroupRomsByGame(createRomOrganizer(args)).groupRomsByGame();
    }

    private void groupRomsByGame() {
        FileUtils.listFiles(romOrganizer.romDirectory)
                .forEach(this::moveRomToGameDirectory);

        FileUtils.listDirectories(romOrganizer.romDirectory)
                .forEach(this::splitGoodAndNotGoodRoms);
    }

    private void moveRomToGameDirectory(Path rom) {
        String game = romOrganizer.romNameHandling.getGame(rom.getFileName().toString());
        FileUtils.moveToSubDirectory(rom, game);
    }

    private void splitGoodAndNotGoodRoms(Path gameDirectory) {
        List<String> gameRomNames = FileUtils.listFiles(gameDirectory).stream()
                .map(file -> file.getFileName().toString())
                .collect(Collectors.toList());
        romOrganizer.romNameHandling.getLowerQualityRomNames(gameRomNames).forEach(romName ->
                FileUtils.moveToSubDirectory(gameDirectory.resolve(romName), romOrganizer.config.getLowerQualityRomsDirectory()));
    }
}
