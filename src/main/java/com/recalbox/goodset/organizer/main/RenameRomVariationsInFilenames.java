package com.recalbox.goodset.organizer.main;

import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;

@RequiredArgsConstructor
public class RenameRomVariationsInFilenames {

    private final RomOrganizer romOrganizer;

    public static void main(String... args) {
        new RenameRomVariationsInFilenames(createRomOrganizer(args))
                .renameRomVariationsInFilenames();
    }

    public void renameRomVariationsInFilenames() {
        FileUtils.listFiles(romOrganizer.romDirectory)
                .forEach(this::renameRom);
    }

    private void renameRom(Path rom) {
        String newName = romOrganizer.romNameHandling.replaceFilenameRomVariations(rom.getFileName().toString());
        if (!rom.getFileName().toString().equals(newName)) {
            FileUtils.renameFileName(rom, newName);
        }
    }
}
