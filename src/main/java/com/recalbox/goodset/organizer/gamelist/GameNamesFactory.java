package com.recalbox.goodset.organizer.gamelist;

import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
class GameNamesFactory {

    public static GameNames createGameNames(GameNameType gameNameType, List<RomInfo> roms, List<String> regionsPreferenceOrder) {
        List<GameName> gameNamesWithoutDecorations = createGameNames(roms, gameNameType);
        GameName gameNameReference = findGameNameReference(gameNamesWithoutDecorations, regionsPreferenceOrder);
        return new GameNames(gameNamesWithoutDecorations, gameNameReference);
    }

    public static List<GameName> createGameNames(List<RomInfo> roms,
                                                 GameNameType gameNameType) {
        Map<String, List<RomInfo>> romsGroupedByNameWithoutDecorations = roms.stream()
                .collect(Collectors.groupingBy(gameNameType.romNameWithoutDecorationsExtractor));
        return romsGroupedByNameWithoutDecorations.entrySet().stream()
                .map(entry -> createGameName(entry, gameNameType, roms))
                .collect(Collectors.toList());
    }

    private static GameName createGameName(Map.Entry<String, List<RomInfo>> entry,
                                           GameNameType gameNameType,
                                           List<RomInfo> allGameRoms) {
        String nameWithoutDecorations = entry.getKey();
        List<RomInfo> gameNameRoms = entry.getValue();
        return new GameName(nameWithoutDecorations, gameNameType, gameNameRoms, allGameRoms.size());
    }

    public static GameName findGameNameReference(List<GameName> gameNames, List<String> regionsPreferenceOrder) {
        List<GameName> gameNameReferences = gameNames;
        for (String region : regionsPreferenceOrder) {
            // Keep only GameNames containing more roms of region
            gameNameReferences = findGameNameReferencesForRegion(region, gameNameReferences);
        }
        // return GameName with high ratio if many still present
        return GameName.getGameNameWithHighestDistributionRatio(gameNameReferences);
    }

    private static List<GameName> findGameNameReferencesForRegion(String region, List<GameName> gameNames) {
        Map<Long, List<GameName>> numberOfGameNamesByRegion = gameNames.stream().collect(Collectors.groupingBy(
                gameName -> gameName.getGameCountries().getOrDefault(region, 0L)
        ));
        return numberOfGameNamesByRegion.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getKey))
                .orElseThrow(IllegalStateException::new)
                .getValue();
    }
}
