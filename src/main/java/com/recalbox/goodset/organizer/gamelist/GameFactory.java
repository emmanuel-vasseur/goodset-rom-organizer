package com.recalbox.goodset.organizer.gamelist;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.recalbox.goodset.organizer.gamelist.RomGatheredType.*;
import static com.recalbox.goodset.organizer.util.Checker.checkArgument;
import static java.util.Objects.requireNonNull;

@UtilityClass
public class GameFactory {

    public static Game create(int gameId, List<RomInfo> roms, List<String> regionsPreferenceOrder) {
        requireNonNull(roms);
        requireNonNull(regionsPreferenceOrder);
        checkArgument(!roms.isEmpty(), "Game must have at least one rom.");
        GameNames gameListGameNames = GameNamesFactory.createGameNames(GameNameType.GAMELIST, roms, regionsPreferenceOrder);
        GameNames romPathsGameNames = GameNamesFactory.createGameNames(GameNameType.ROMPATH, roms, regionsPreferenceOrder);
        RomGatheredType romsGatheredType = computeRomGatheredType(gameListGameNames.getGameNameList(), romPathsGameNames.getGameNameList());
        return new Game(gameId, roms, romsGatheredType, gameListGameNames, romPathsGameNames);
    }

    private static RomGatheredType computeRomGatheredType(List<GameName> namesWithoutDecorationsFromGameList,
                                                          List<GameName> namesWithoutDecorationsFromRomPaths) {
        Set<String> namesWithoutDecorationFromRomPaths = namesWithoutDecorationsFromRomPaths.stream()
                .map(GameName::getGameNameWithoutDecorations)
                .collect(Collectors.toSet());
        Set<String> namesWithoutDecorationFromGameList = namesWithoutDecorationsFromGameList.stream()
                .map(GameName::getGameNameWithoutDecorations)
                .collect(Collectors.toSet());

        boolean romsGatheredWithOneNameInPaths = namesWithoutDecorationFromRomPaths.size() == 1;
        boolean romsGatheredWithOneNameInGameList = namesWithoutDecorationFromGameList.size() == 1;
        boolean sameNamesInGameListAndPaths = namesWithoutDecorationFromGameList.equals(namesWithoutDecorationFromRomPaths);

        if (sameNamesInGameListAndPaths) {
            return romsGatheredWithOneNameInPaths ? SAME_UNIQUE_NAME_IN_GAMELIST_AND_PATHS : SAME_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS;
        }
        if (romsGatheredWithOneNameInPaths) {
            return romsGatheredWithOneNameInGameList ? DIFFERENT_UNIQUE_NAME_IN_GAMELIST_AND_PATHS : UNIQUE_NAME_ONLY_IN_PATHS;
        }
        return romsGatheredWithOneNameInGameList ? UNIQUE_NAME_ONLY_IN_GAMELIST : DIFFERENT_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS;
    }

}
