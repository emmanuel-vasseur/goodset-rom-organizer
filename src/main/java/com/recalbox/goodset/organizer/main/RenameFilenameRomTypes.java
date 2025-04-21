package com.recalbox.goodset.organizer.main;

import static com.recalbox.goodset.organizer.main.CommandLineRunnerInitialization.createRomOrganizer;

public class RenameFilenameRomTypes {

    public static void main(String... args) {
        createRomOrganizer(args).renameFilenameRomTypes();
    }
}
