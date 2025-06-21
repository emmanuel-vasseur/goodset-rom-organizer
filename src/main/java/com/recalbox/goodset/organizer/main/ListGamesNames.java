package com.recalbox.goodset.organizer.main;

import static com.recalbox.goodset.organizer.main.CommandLineRunnerInitializer.createRomOrganizer;

public class ListGamesNames {

    public static void main(String... args) {
        createRomOrganizer(args).listGamesNames();
    }
}
