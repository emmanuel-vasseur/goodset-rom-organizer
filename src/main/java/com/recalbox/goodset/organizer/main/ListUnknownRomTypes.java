package com.recalbox.goodset.organizer.main;

import static com.recalbox.goodset.organizer.main.CommandLineRunnerInitialization.createRomOrganizer;

public class ListUnknownRomTypes {

    public static void main(String... args) {
        createRomOrganizer(args).listUnknownRomTypes();
    }
}
