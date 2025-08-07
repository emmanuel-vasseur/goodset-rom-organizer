package com.recalbox.goodset.organizer.gamelist;

import com.recalbox.goodset.organizer.util.MapUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class GameList {
    private static final Comparator<Game> NON_OBVIOUS_GAME_REFERENCES_COMPARATOR = Comparator.<Game, Boolean>
                    comparing(game -> game.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio())
            .thenComparing(game -> game.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio())
            .thenComparing(Game::getGameNameReferenceDistributionRatio);

    private final List<Game> games;

    public Game getGame(int gameId) {
        return games.stream()
                .filter(game -> game.getGameId() == gameId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Game with id " + gameId + " not found."));
    }

    public List<Game> getGamesSortedByNonObviousGameReferences() {
        return games.stream()
                .sorted(NON_OBVIOUS_GAME_REFERENCES_COMPARATOR)
                .collect(Collectors.toList());
    }

    public Map<String, Set<Game>> getConflictingGameNames(GameNameType gameNameType) {
        Map<String, Set<Game>> gamesGroupedByGameNameReference = groupGamesByGameNameReference(gameNameType);
        return MapUtils.removeEntriesWithSingleElement(gamesGroupedByGameNameReference);
    }

    public Map<String, Set<Game>> getConflictingGameNames() {
        Map<String, Set<Game>> mergedGroupedGames = MapUtils.mergeMaps(
                groupGamesByGameNameReference(GameNameType.GAMELIST),
                groupGamesByGameNameReference(GameNameType.ROMPATH));
        return MapUtils.removeEntriesWithSingleElement(mergedGroupedGames);
    }

    private Map<String, Set<Game>> groupGamesByGameNameReference(GameNameType gameNameType) {
        return games.stream()
                .filter(Game::isRecognizedInGameList)
                .collect(Collectors.groupingBy(
                        game -> gameNameType.gameNamesExtractor.apply(game)
                                .getGameNameReference().getGameNameWithoutDecorations(),
                        Collectors.toSet())
                );
    }

    public List<Set<Game>> getGamesToMerge() {
        return getGamesToMerge(getConflictingGameNames());
    }

    public List<Set<Game>> getGamesToMerge(GameNameType gameNameType) {
        return getGamesToMerge(getConflictingGameNames(gameNameType));
    }

    private List<Set<Game>> getGamesToMerge(Map<String, Set<Game>> remainingGameNames) {
        Map<Game, Set<String>> remainingGames = MapUtils.reverseMultiMap(remainingGameNames);
        List<Set<Game>> result = new ArrayList<>();

        while (!remainingGameNames.isEmpty()) {
            Map.Entry<String, Set<Game>> currentGamesToMerge = remainingGameNames.entrySet().iterator().next();
            remainingGameNames.remove(currentGamesToMerge.getKey());
            result.add(getRecursivelyGamesToMerge(
                    currentGamesToMerge.getValue(),
                    remainingGameNames,
                    remainingGames));
        }
        return result;
    }

    private Set<Game> getRecursivelyGamesToMerge(Set<Game> currentGamesToMerge,
                                                 Map<String, Set<Game>> remainingGameNames,
                                                 Map<Game, Set<String>> remainingGames) {

        Set<String> linkedGameNames = MapUtils.getValuesOfKeys(currentGamesToMerge, remainingGames);
        remainingGames.keySet().removeAll(currentGamesToMerge);

        if (linkedGameNames.isEmpty()) {
            return currentGamesToMerge;
        }

        Set<Game> linkedGames = MapUtils.getValuesOfKeys(linkedGameNames, remainingGameNames);
        remainingGameNames.keySet().removeAll(linkedGameNames);

        if (linkedGames.isEmpty()) {
            return currentGamesToMerge;
        }

        return MapUtils.mergeSets(currentGamesToMerge,
                getRecursivelyGamesToMerge(linkedGames, remainingGameNames, remainingGames));
    }

}
