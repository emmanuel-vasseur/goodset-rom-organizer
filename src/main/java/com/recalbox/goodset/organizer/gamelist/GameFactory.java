package com.recalbox.goodset.organizer.gamelist;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.recalbox.goodset.organizer.gamelist.RomGatheredType.*;
import static com.recalbox.goodset.organizer.util.Checker.checkArgument;

@UtilityClass
public class GameFactory {

    public static Game create(int gameId, List<RomInfo> roms) {
        checkArgument(!roms.isEmpty(), "Game must have at least one rom.");
        List<GameName> namesWithoutDecorationsFromGameList = getNamesWithoutDecorationsFromGameList(roms);
        List<GameName> namesWithoutDecorationsFromRomPaths = getNamesWithoutDecorationsFromRomPaths(roms);
        RomGatheredType romsGatheredType = computeRomGatheredType(namesWithoutDecorationsFromGameList, namesWithoutDecorationsFromRomPaths);
        return new Game(gameId, roms, romsGatheredType, namesWithoutDecorationsFromGameList, namesWithoutDecorationsFromRomPaths);
    }

    private static List<GameName> getNamesWithoutDecorationsFromGameList(List<RomInfo> roms) {
        return getNamesWithoutDecorations(roms, GameNameType.GAMELIST);
    }

    private static List<GameName> getNamesWithoutDecorationsFromRomPaths(List<RomInfo> roms) {
        return getNamesWithoutDecorations(roms, GameNameType.ROMPATH);
    }

    private static List<GameName> getNamesWithoutDecorations(List<RomInfo> roms,
                                                             GameNameType gameNameType) {
        return roms.stream()
                .collect(Collectors.groupingBy(gameNameType.romNameWithoutDecorationsExtractor))
                .entrySet().stream()
                .map(entry -> createGameName(entry, gameNameType, roms.size()))
                .sorted(GameName.RATIO_COMPARATOR)
                .collect(Collectors.toList());
    }

    private static GameName createGameName(Map.Entry<String, List<RomInfo>> entry,
                                           GameNameType gameNameType,
                                           int totalNumberOfGameRoms) {
        String nameWithoutDecorations = entry.getKey();
        List<RomInfo> gameNameRoms = entry.getValue();
        return new GameName(nameWithoutDecorations, gameNameType, gameNameRoms, totalNumberOfGameRoms);
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
