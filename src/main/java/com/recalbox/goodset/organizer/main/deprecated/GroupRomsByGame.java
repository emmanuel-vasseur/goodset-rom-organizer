package com.recalbox.goodset.organizer.main.deprecated;

import static com.recalbox.goodset.organizer.main.CommandLineRunnerInitializer.createRomOrganizer;

public class GroupRomsByGame {

    public static void main(String... args) {
        createRomOrganizer(args).groupRomsByGame();
    }
}
