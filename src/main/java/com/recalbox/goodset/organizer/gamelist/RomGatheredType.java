package com.recalbox.goodset.organizer.gamelist;

import java.util.EnumSet;
import java.util.Set;

public enum RomGatheredType {

    SAME_UNIQUE_NAME_IN_GAMELIST_AND_PATHS,
    DIFFERENT_UNIQUE_NAME_IN_GAMELIST_AND_PATHS,
    UNIQUE_NAME_ONLY_IN_GAMELIST,
    UNIQUE_NAME_ONLY_IN_PATHS,
    SAME_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS,
    DIFFERENT_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS,
    ;

    private static final Set<RomGatheredType> UNIQUE_NAME_TYPES = EnumSet.of(
            SAME_UNIQUE_NAME_IN_GAMELIST_AND_PATHS,
            DIFFERENT_UNIQUE_NAME_IN_GAMELIST_AND_PATHS,
            UNIQUE_NAME_ONLY_IN_GAMELIST,
            UNIQUE_NAME_ONLY_IN_PATHS
    );

    public boolean isGatheredWithOnlyOneName() {
        return UNIQUE_NAME_TYPES.contains(this);
    }
}
