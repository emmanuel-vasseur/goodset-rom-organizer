package com.recalbox.goodset.organizer.main;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;

public class ListUnknownRomVariationsInFilenames {

    public static void main(String... args) {
        new ListUnknownRomVariations(createRomOrganizer(args))
                .listUnknownRomVariationsInFilenames();
    }

}
