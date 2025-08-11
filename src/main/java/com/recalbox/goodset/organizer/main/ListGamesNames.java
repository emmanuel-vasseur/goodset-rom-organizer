package com.recalbox.goodset.organizer.main;

import com.recalbox.goodset.organizer.gamelist.*;
import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.MapUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.*;
import java.util.stream.Collectors;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;

@RequiredArgsConstructor
@Log
public class ListGamesNames {

    private final RomOrganizer romOrganizer;

    public static void main(String... args) {
        new ListGamesNames(createRomOrganizer(args)).listGamesNames();
    }

    public void listGamesNames() {
        GameList gameList = romOrganizer.createGameList();
        List<Game> sortedGames = gameList.getGamesSortedByNonObviousGameReferences();

        logConflictingGameNames(gameList.findConflictingGameNames(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logConflictingGameNames(gameList.findConflictingGameNames(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logConflictingGameNames(gameList.findConflictingGameNames(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findGamesToMerge(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logGamesToMerge(gameList.findGamesToMerge(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findGamesToMerge(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        log.info("** REVIEW AND FIX THESE POTENTIAL PROBLEMATIC GAMES");
        logConflictingGameNames(gameList.findPotentialProblematicGameNames(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logConflictingGameNames(gameList.findPotentialProblematicGameNames(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logConflictingGameNames(gameList.findPotentialProblematicGameNames(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findPotentialProblematicGamesToMerge(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logGamesToMerge(gameList.findPotentialProblematicGamesToMerge(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findPotentialProblematicGamesToMerge(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);

        logSeparator();

        log.info("** GAMES HAVING SAME NAMES WITHOUT DECORATION");
        logConflictingGameNames(gameList.findGamesHavingSameNamesWithoutDecoration(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logConflictingGameNames(gameList.findGamesHavingSameNamesWithoutDecoration(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logConflictingGameNames(gameList.findGamesHavingSameNamesWithoutDecoration(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findGamesToMerge2(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logGamesToMerge(gameList.findGamesToMerge2(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findGamesToMerge2(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        log.info("** REVIEW AND FIX THESE POTENTIAL PROBLEMATIC GAMES");
        logConflictingGameNames(gameList.findPotentialProblematicGamesHavingSameNamesWithoutDecoration(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logConflictingGameNames(gameList.findPotentialProblematicGamesHavingSameNamesWithoutDecoration(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logConflictingGameNames(gameList.findPotentialProblematicGamesHavingSameNamesWithoutDecoration(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findPotentialProblematicGamesToMerge2(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logGamesToMerge(gameList.findPotentialProblematicGamesToMerge2(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.findPotentialProblematicGamesToMerge2(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);

        logSeparator();
        logNotRecognizedRoms(sortedGames);
        logTopGamesWithHighestRoms(gameList);

        Map<RomGatheredType, List<Game>> gamesWithOnlyOneRom = sortedGames.stream()
                .filter(Game::isRecognizedInGameList)
                .filter(Game::containOnlyOneRom)
                .collect(Collectors.groupingBy(Game::getRomGatheredType));
        Map<RomGatheredType, List<Game>> gamesWithMultipleRoms = sortedGames.stream()
                .filter(Game::isRecognizedInGameList)
                .filter(game -> !game.containOnlyOneRom())
                .collect(Collectors.groupingBy(Game::getRomGatheredType));

        log.info("** games with only one rom distribution:");
        gamesWithOnlyOneRom.forEach((gatheredType, roms) ->
                log.info(String.format("%s: %s games", gatheredType, roms.size())));
        log.info("** games with multiple roms distribution:");
        gamesWithMultipleRoms.forEach((gatheredType, roms) ->
                log.info(String.format("%s: %s games", gatheredType, roms.size())));

        gamesWithMultipleRoms.entrySet().stream()
                .filter(entry -> !entry.getKey().isGatheredWithOnlyOneName())
                .forEach(ListGamesNames::logGamesWithDifferentNames);

        logSeparator();

        Map<String, Set<Game>> sameGameNamesWithDecoration = gameList.findSameGameNamesWithDecoration();
        log.info(String.format("** %s same game names:", sameGameNamesWithDecoration.size()));
        sameGameNamesWithDecoration.entrySet().stream()
                .sorted(Comparator.comparing(e -> new TreeSet<>(getGameIds(e.getValue())).last()))
                .forEach(e -> logSameGameNamesWithDecoration(e.getKey(), e.getValue()));
    }

    private static void logTopGamesWithHighestRoms(GameList gameList) {
        log.info("** Top 20 games with most ROMs:");
        gameList.getTopGamesWithHighestRoms(20).forEach(game ->
                log.info(String.format("Game %s: %s ROMs - %s (%s different names) - %s (%s different names)",
                        game.getGameId(), game.getRoms().size(),
                        game.getGameListGameNames().getGameNameReference().getGameNameWithoutDecorations(),
                        game.getGameListGameNames().getGameNameList().size(),
                        game.getRomPathsGameNames().getGameNameReference().getGameNameWithoutDecorations(),
                        game.getRomPathsGameNames().getGameNameList().size())));
    }

    private static void logNotRecognizedRoms(List<Game> sortedGames) {
        sortedGames.stream()
                .filter(Game::isNotRecognizedInGameList)
                .forEach(game -> {
                    int romNumber = game.getRoms().size();
                    int nameNumber = game.getRomPathsGameNames().getGameNameList().size();
                    log.info(String.format("%s different names (%s roms) in filesystem without gamelist information", nameNumber, romNumber));
                });
    }

    private static void logSeparator() {
        log.info("");
        log.info("********************************************************************************");
        log.info("");
    }

    private static void logSameGameNamesWithDecoration(String gameName, Collection<Game> games) {
        List<RomInfo> sameGamesRomInfo = games.stream()
                .flatMap(game -> game.getRoms().stream())
                .filter(rom -> gameName.equals(rom.getName()))
                .collect(Collectors.toList());
        String formattedSameGamesRomInfo = sameGamesRomInfo.stream()
                .map(rom -> String.format("id: %s, fileName: %s", rom.getGameId(), rom.getFileName()))
                .collect(Collectors.joining(" - "));
        log.info(String.format("- found %s occurrences in %s games (%s) of game name \"%s\": %s",
                sameGamesRomInfo.size(), games.size(), getGameIds(games), gameName, formattedSameGamesRomInfo));
    }

    private static void logConflictingGameNames(Map<String, Set<Game>> conflictingGameNames, String sourceType) {
        List<Map.Entry<String, Set<Game>>> gamesWithOnlyNotRecognizedConflicts = new TreeMap<>(conflictingGameNames).entrySet().stream()
                .filter(entry -> entry.getValue().stream().filter(Game::isRecognizedInGameList).count() <= 1)
                .collect(Collectors.toList());
        log.info(String.format("** games with only not recognized conflicts in %s: %s", sourceType, gamesWithOnlyNotRecognizedConflicts.size()));
        gamesWithOnlyNotRecognizedConflicts.forEach(e ->
                log.info(String.format("- %s in %s games: %s", e.getKey(), e.getValue().size(), formatGames(e.getValue(), sourceType))));

        List<Map.Entry<String, Set<Game>>> gameWithNameConflicts = new TreeMap<>(conflictingGameNames).entrySet().stream()
                .filter(entry -> entry.getValue().stream().filter(Game::isRecognizedInGameList).count() > 1)
                .collect(Collectors.toList());
        log.info(String.format("** games with name conflicts in %s: %s", sourceType, gameWithNameConflicts.size()));
        gameWithNameConflicts.forEach(e ->
                log.info(String.format("- %s in %s games: %s", e.getKey(), e.getValue().size(), formatGames(e.getValue(), sourceType))));
    }

    private static void logGamesToMerge(List<Set<Game>> gamesToMerge, String sourceType) {
        List<Set<Game>> gamesWithOnlyNotRecognizedConflicts = gamesToMerge.stream()
                .filter(games -> games.stream().filter(Game::isRecognizedInGameList).count() <= 1)
                .collect(Collectors.toList());
        log.info(String.format("** list of games with only not recognized conflicts to merge in %s: %s", sourceType, gamesWithOnlyNotRecognizedConflicts.size()));
        gamesWithOnlyNotRecognizedConflicts.forEach(games ->
                log.info(String.format("- %s games to merge: %s", games.size(), formatGames(games, sourceType))));

        List<Set<Game>> gameWithNameConflicts = gamesToMerge.stream()
                .filter(games -> games.stream().filter(Game::isRecognizedInGameList).count() > 1)
                .collect(Collectors.toList());
        log.info(String.format("** list of games name conflicts to merge in %s: %s", sourceType, gameWithNameConflicts.size()));
        gameWithNameConflicts.forEach(games ->
                log.info(String.format("- %s games to merge: %s", games.size(), formatGames(games, sourceType))));
    }

    private static String formatGames(Set<Game> games, String sourceType) {
        List<Set<String>> reconizedGameNamesList = games.stream()
                .filter(Game::isRecognizedInGameList)
                .map(game -> getAllGameNamesWithoutDecorations(game, sourceType))
                .collect(Collectors.toList());
        int totalGameNames = reconizedGameNamesList.stream().mapToInt(Set::size).sum();
        String formattedGameNamesCount = reconizedGameNamesList.stream()
                .map(gameNames -> String.format("%s", gameNames.size()))
                .collect(Collectors.joining(" - "));
        String formattedGameNames = reconizedGameNamesList.stream()
                .map(gameNames -> String.format("%s", gameNames))
                .collect(Collectors.joining(" - "));
        return String.format("gameIds: %s - %s total gameNames (%s): %s",
                getGameIds(games), totalGameNames, formattedGameNamesCount, formattedGameNames);
    }

    private static List<Integer> getGameIds(Collection<Game> games) {
        return games.stream().map(Game::getGameId).collect(Collectors.toList());
    }

    private static Set<String> getAllGameNamesWithoutDecorations(Game game, String sourceType) {
        if (GameNameType.GAMELIST.sourceType.equals(sourceType)) {
            return game.getGameListGameNames().getAllGameNamesWithoutDecorations();
        }
        if (GameNameType.ROMPATH.sourceType.equals(sourceType)) {
            return game.getRomPathsGameNames().getAllGameNamesWithoutDecorations();
        }
        return MapUtils.mergeSets(game.getGameListGameNames().getAllGameNamesWithoutDecorations(),
                game.getRomPathsGameNames().getAllGameNamesWithoutDecorations());
    }

    private static void logGamesWithDifferentNames(Map.Entry<RomGatheredType, List<Game>> entry) {
        logSeparator();
        log.info(String.format("** details for %s:", entry.getKey()));
        entry.getValue().forEach(ListGamesNames::logGameWithDifferentNames);
    }

    private static void logGameWithDifferentNames(Game game) {
        log.info(String.format("gameId %s", game.getGameId()));
        logDifferentNames(game, GameNameType.GAMELIST);
        logDifferentNames(game, GameNameType.ROMPATH);
    }

    private static void logDifferentNames(Game game, GameNameType gameNameType) {
        GameNames gameNames = gameNameType.gameNamesExtractor.apply(game);
        String gameNameReferenceStatus = gameNames.gameNameReferenceHasHighestDistributionRatio() ?
                "reference HAS highest ratio" : "reference DONT HAVE highest ratio";

        String formattedGameNames = gameNames.getGameNamesStartedByReferenceSortedByDistributionRatio().stream()
                .map(gameName -> String.format("%s (%s roms, %.2f%%, countries: %s)",
                        gameName.getGameNameWithoutDecorations(),
                        gameName.getAllNamesWithDecorations().size(), gameName.getDistributionRatioInGame() * 100,
                        gameName.getGameCountries()))
                .collect(Collectors.joining(", "));

        log.info(String.format("- %s names in %s, %s: %s",
                gameNames.getGameNameList().size(), gameNameType.sourceType, gameNameReferenceStatus, formattedGameNames));
    }
}
