package com.recalbox.goodset.organizer.gamelist;

import com.recalbox.goodset.organizer.util.MapUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class GameList {
    private static final Comparator<Game> NON_OBVIOUS_GAME_REFERENCES_COMPARATOR = Comparator.<Game, Boolean>
                    comparing(game -> game.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio())
            .thenComparing(game -> game.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio())
            .thenComparing(Game::getGameNameReferenceDistributionRatio);

    private static final Comparator<Game> HIGHEST_ROM_GAMES_COMPARATOR = Comparator.<Game>
            comparingInt(game -> game.getRoms().size()).reversed();

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

    public List<Game> getTopGamesWithHighestRoms(int n) {
        return games.stream()
                .sorted(HIGHEST_ROM_GAMES_COMPARATOR)
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<String, Set<Game>> findConflictingGameNames(GameNameType gameNameType) {
        Map<String, Set<Game>> gamesGroupedByGameNameReference = groupGamesByGameNameReference(gameNameType);
        return MapUtils.removeEntriesWithSingleElement(gamesGroupedByGameNameReference);
    }

    public Map<String, Set<Game>> findConflictingGameNames() {
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

    public List<Set<Game>> findGamesToMerge() {
        return findGamesToMerge(findConflictingGameNames());
    }

    public List<Set<Game>> findGamesToMerge(GameNameType gameNameType) {
        return findGamesToMerge(findConflictingGameNames(gameNameType));
    }

    private List<Set<Game>> findGamesToMerge(Map<String, Set<Game>> gameNamesInMultipleGames) {
        Map<Game, Set<String>> gamesHavingSameNames = MapUtils.reverseSetMultiMap(gameNamesInMultipleGames);
        List<Set<Game>> result = new ArrayList<>();

        while (!gameNamesInMultipleGames.isEmpty()) {
            Map.Entry<String, Set<Game>> currentGamesToMerge = gameNamesInMultipleGames.entrySet().iterator().next();
            gameNamesInMultipleGames.remove(currentGamesToMerge.getKey());
            result.add(findRecursivelyGamesToMerge(
                    currentGamesToMerge.getValue(),
                    gameNamesInMultipleGames,
                    gamesHavingSameNames));
        }
        return result;
    }

    private Set<Game> findRecursivelyGamesToMerge(Set<Game> currentGamesToMerge,
                                                  Map<String, Set<Game>> remainingGameNames,
                                                  Map<Game, Set<String>> remainingGames) {

        Set<Game> currentRecognizedGamesToMerge = currentGamesToMerge.stream().filter(Game::isRecognizedInGameList).collect(Collectors.toSet());
        Set<String> linkedGameNames = MapUtils.getAllValues(currentRecognizedGamesToMerge, remainingGames);
        remainingGames.keySet().removeAll(currentRecognizedGamesToMerge);

        if (linkedGameNames.isEmpty()) {
            return currentGamesToMerge;
        }

        Set<Game> linkedGames = MapUtils.getAllValues(linkedGameNames, remainingGameNames);
        remainingGameNames.keySet().removeAll(linkedGameNames);

        if (linkedGames.isEmpty()) {
            return currentGamesToMerge;
        }

        return MapUtils.mergeSets(currentGamesToMerge,
                findRecursivelyGamesToMerge(linkedGames, remainingGameNames, remainingGames));
    }

    public Map<String, Set<Game>> findPotentialProblematicGameNames() {
        return findPotentialProblematicGameNames(findConflictingGameNames(), findGamesToMerge());
    }

    public Map<String, Set<Game>> findPotentialProblematicGameNames(GameNameType gameNameType) {
        return findPotentialProblematicGameNames(findConflictingGameNames(gameNameType), findGamesToMerge(gameNameType));
    }

    private Map<String, Set<Game>> findPotentialProblematicGameNames(Map<String, Set<Game>> conflictingGameNames, List<Set<Game>> gamesToMerge) {
        conflictingGameNames.values().removeAll(gamesToMerge);
        return conflictingGameNames;
    }

    public List<Set<Game>> findPotentialProblematicGamesToMerge() {
        return findPotentialProblematicGamesToMerge(findGamesToMerge(), findConflictingGameNames());
    }

    public List<Set<Game>> findPotentialProblematicGamesToMerge(GameNameType gameNameType) {
        return findPotentialProblematicGamesToMerge(findGamesToMerge(gameNameType), findConflictingGameNames(gameNameType));
    }

    private List<Set<Game>> findPotentialProblematicGamesToMerge(List<Set<Game>> gamesToMerge, Map<String, Set<Game>> conflictingGameNames) {
        gamesToMerge.removeAll(conflictingGameNames.values());
        return gamesToMerge;
    }

    public Map<String, Set<Game>> findSameGameNamesWithDecoration() {
        Map<Game, List<String>> allGameNamesWithDecoration = games.stream().collect(Collectors.toMap(
                Function.identity(),
                game -> game.getGameListGameNames().getAllGameNamesWithDecorations()
        ));
        Map<String, List<Game>> sameGameNamesWithDecoration = MapUtils.removeEntriesWithSingleElement(
                MapUtils.reverseListMultiMap(allGameNamesWithDecoration));
        return sameGameNamesWithDecoration.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new HashSet<>(e.getValue())
                ));
    }

    public Map<String, Set<Game>> findGamesHavingSameNamesWithoutDecoration() {
        Map<String, Set<Game>> allGameNamesWithoutDecoration = MapUtils.mergeMaps(
                getAllGameNamesWithoutDecoration(GameNameType.GAMELIST),
                getAllGameNamesWithoutDecoration(GameNameType.ROMPATH));
        return MapUtils.removeEntriesWithSingleElement(allGameNamesWithoutDecoration);
    }

    public Map<String, Set<Game>> findGamesHavingSameNamesWithoutDecoration(GameNameType gameNameType) {
        Map<String, Set<Game>> allGameNamesWithoutDecoration = getAllGameNamesWithoutDecoration(gameNameType);
        return MapUtils.removeEntriesWithSingleElement(allGameNamesWithoutDecoration);
    }

    private Map<String, Set<Game>> getAllGameNamesWithoutDecoration(GameNameType gameNameType) {
        Map<Game, Set<String>> allGameNamesWithoutDecoration = games.stream().collect(Collectors.toMap(
                Function.identity(),
                game -> gameNameType.gameNamesExtractor.apply(game).getAllGameNamesWithoutDecorations()
        ));
        return MapUtils.reverseSetMultiMap(allGameNamesWithoutDecoration);
    }

    public List<Set<Game>> findGamesToMerge2() {
        return findGamesToMerge(findGamesHavingSameNamesWithoutDecoration());
    }

    public List<Set<Game>> findGamesToMerge2(GameNameType gameNameType) {
        return findGamesToMerge(findGamesHavingSameNamesWithoutDecoration(gameNameType));
    }

    public Map<String, Set<Game>> findPotentialProblematicGamesHavingSameNamesWithoutDecoration() {
        return findPotentialProblematicGameNames(findGamesHavingSameNamesWithoutDecoration(), findGamesToMerge2());
    }

    public Map<String, Set<Game>> findPotentialProblematicGamesHavingSameNamesWithoutDecoration(GameNameType gameNameType) {
        return findPotentialProblematicGameNames(findGamesHavingSameNamesWithoutDecoration(gameNameType), findGamesToMerge2(gameNameType));
    }

    public List<Set<Game>> findPotentialProblematicGamesToMerge2() {
        return findPotentialProblematicGamesToMerge(findGamesToMerge2(), findGamesHavingSameNamesWithoutDecoration());
    }

    public List<Set<Game>> findPotentialProblematicGamesToMerge2(GameNameType gameNameType) {
        return findPotentialProblematicGamesToMerge(findGamesToMerge(gameNameType), findGamesHavingSameNamesWithoutDecoration(gameNameType));
    }
}
