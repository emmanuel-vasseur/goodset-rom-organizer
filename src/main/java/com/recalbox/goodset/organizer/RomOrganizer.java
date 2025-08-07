package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.ConfigProperties;
import com.recalbox.goodset.organizer.gamelist.*;
import com.recalbox.goodset.organizer.util.Checker;
import com.recalbox.goodset.organizer.util.FileUtils;
import com.recalbox.goodset.organizer.util.MapUtils;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.recalbox.goodset.organizer.util.UncheckedIOExceptionThrower.rethrowIOException;

@Log
public class RomOrganizer {

    private final RomNameHandling romNameHandling = new RomNameHandling();
    private final ConfigProperties config = new ConfigProperties();

    private final Path romDirectory;
    private final Path gameListFile;

    public RomOrganizer(String romDirectory) {
        this.romDirectory = Paths.get(romDirectory).toAbsolutePath().normalize();
        this.gameListFile = this.romDirectory.resolve(config.getGameListFilename());

        Checker.checkArgument(Files.isDirectory(this.romDirectory), "Directory '%s' does not exits", this.romDirectory);
    }

    public void listUnknownRomVariationsInFilenames() {
        Stream<String> romNames = FileUtils.listFiles(romDirectory).stream().map(rom -> rom.getFileName().toString());
        Map<String, List<String>> romNamesWithUnknownVariations = romNameHandling.getRomNamesWithUnknownRomVariations(romNames, romNameHandling::getFilenameUnknownRomVariations);
        logRomNamesWithUnknownRomVariations(romNamesWithUnknownVariations, romDirectory);
    }

    public void listUnknownRomVariationsInGameList() {
        GameListTransformer gameListTransformer = createGameListTransformer();
        Map<String, List<String>> romNamesWithUnknownVariations = gameListTransformer.getGameListRomNamesWithUnknownRomVariations();
        logRomNamesWithUnknownRomVariations(romNamesWithUnknownVariations, gameListFile);
    }

    private void logRomNamesWithUnknownRomVariations(Map<String, List<String>> romNamesWithUnknownVariations, Path location) {
        logMapOfList(romNamesWithUnknownVariations, location, "roms with unknown rom types", "unknown types");
        Map<String, List<String>> unknownRomVariationsWithRomNames = inverseMapOfList(romNamesWithUnknownVariations);
        logMapOfList(unknownRomVariationsWithRomNames, location, "unknown rom types", "roms");
    }

    private void logMapOfList(Map<String, List<String>> mapOfList,
                              Path location, String title, String mapEntryTitle) {
        if (mapOfList.isEmpty()) {
            log.info(String.format("** Result OK, No %s found in '%s' **", title, location));
        } else {
            log.warning(String.format("******** %s %s found in '%s' ********",
                    mapOfList.size(), title, location));
            mapOfList.forEach((entryKey, entryValues) ->
                    log.warning(String.format("%s %s - %s : %s",
                            entryValues.size(), mapEntryTitle, entryKey, String.join(", ", entryValues))));
        }
    }

    private Map<String, List<String>> inverseMapOfList(Map<String, List<String>> mapOfList) {
        return mapOfList.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream() // transform Entry<String, List<String>> to List<Entry<String, String>>
                        .map(value -> new AbstractMap.SimpleEntry<>(entry.getKey(), value)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue, // value becomes the key
                        TreeMap::new, // sort result
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList()) // group associated keys in list
                ));
    }

    public void renameRomVariationsInFilenames() {
        FileUtils.listFiles(romDirectory)
                .forEach(this::renameRom);
    }

    private void renameRom(Path rom) {
        String newName = romNameHandling.replaceFilenameRomVariations(rom.getFileName().toString());
        if (!rom.getFileName().toString().equals(newName)) {
            FileUtils.renameFileName(rom, newName);
        }
    }

    public void renameRomVariationsInGameList() {
        GameListTransformer gameListTransformer = createGameListTransformer();
        gameListTransformer.renameRomVariations();
        FileUtils.writeLinesIntoFile(gameListTransformer.getGameListContent(), gameListFile);
    }

    public void groupRomsByGame() {
        FileUtils.listFiles(romDirectory)
                .forEach(this::moveRomToGameDirectory);

        FileUtils.listDirectories(romDirectory)
                .forEach(this::splitGoodAndNotGoodRoms);
    }

    private void moveRomToGameDirectory(Path rom) {
        String game = romNameHandling.getGame(rom.getFileName().toString());
        FileUtils.moveToSubDirectory(rom, game);
    }

    private void splitGoodAndNotGoodRoms(Path gameDirectory) {
        List<String> gameRomNames = FileUtils.listFiles(gameDirectory).stream()
                .map(file -> file.getFileName().toString())
                .collect(Collectors.toList());
        romNameHandling.getLowerQualityRomNames(gameRomNames).forEach(romName ->
                FileUtils.moveToSubDirectory(gameDirectory.resolve(romName), config.getLowerQualityRomsDirectory()));
    }

    public void changeFolderImageInGameList() {
        GameListTransformer gameListTransformer = createGameListTransformer();

        List<String> folderImagesToDelete = gameListTransformer.getFolderImagesThatWillBeReplaced();
        gameListTransformer.changeFolderImagesByRomImages();

        FileUtils.writeLinesIntoFile(gameListTransformer.getGameListContent(), gameListFile);
        folderImagesToDelete.forEach(this::deleteFileAndAllEmptyParentDirectories);
    }

    private void deleteFileAndAllEmptyParentDirectories(String folderImage) {
        Path folderImageFile = romDirectory.resolve(folderImage);
        FileUtils.deleteFileAndAllEmptyParentDirectories(folderImageFile);
    }

    private GameListTransformer createGameListTransformer() {
        List<String> gameListContent = loadGameListContent();
        return new GameListTransformer(gameListContent, romNameHandling, config);
    }

    public void moveAllRomsInParentRomDirectory() {
        FileUtils.listDirectories(romDirectory)
                .forEach(this::moveAllRomsInParentRomDirectory);
    }

    private void moveAllRomsInParentRomDirectory(Path subDirectory) {
        rethrowIOException(() -> Files.walkFileTree(subDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                Files.move(path, romDirectory.resolve(path.getFileName()));
                return super.visitFile(path, basicFileAttributes);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                Files.delete(path);
                log.info(String.format("Subdirectory '%s' processed", path));
                return super.postVisitDirectory(path, e);
            }
        }));
    }

    public void listGamesNames() {
        GameList gameList = createGameList();
        List<Game> sortedGames = gameList.getGamesSortedByNonObviousGameReferences();

        logConflictingGameNames(gameList.getConflictingGameNames(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logConflictingGameNames(gameList.getConflictingGameNames(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logConflictingGameNames(gameList.getConflictingGameNames(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.getGamesToMerge(GameNameType.GAMELIST), GameNameType.GAMELIST.sourceType);
        logGamesToMerge(gameList.getGamesToMerge(GameNameType.ROMPATH), GameNameType.ROMPATH.sourceType);
        logGamesToMerge(gameList.getGamesToMerge(),
                GameNameType.GAMELIST.sourceType + " merged with " + GameNameType.ROMPATH.sourceType);

        sortedGames.stream()
                .filter(game -> !game.isRecognizedInGameList())
                .forEach(game -> {
                    int romNumber = game.getRoms().size();
                    int nameNumber = game.getRomPathsGameNames().getGameNameList().size();
                    log.info(String.format("%s different names (%s roms) in filesystem without gamelist information", nameNumber, romNumber));
                });

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
                .forEach(RomOrganizer::logGamesWithDifferentNames);
    }

    private static void logConflictingGameNames(Map<String, Set<Game>> conflictingGameNames, String sourceType) {
        log.info(String.format("** games with name conflicts in %s: %s", sourceType, conflictingGameNames.size()));
        new TreeMap<>(conflictingGameNames).forEach((k, v) ->
                log.info(String.format("- %s in %s games: %s", k, v.size(), formatGames(v, sourceType))));
    }

    private static void logGamesToMerge(List<Set<Game>> gamesToMerge, String sourceType) {
        log.info(String.format("** list of games to merge in %s: %s", sourceType, gamesToMerge.size()));
        gamesToMerge.forEach(games ->
                log.info(String.format("- %s games to merge: %s", games.size(), formatGames(games, sourceType))));
    }

    private static String formatGames(Set<Game> games, String sourceType) {
        List<Integer> gameIds = games.stream()
                .map(Game::getGameId)
                .collect(Collectors.toList());
        List<Set<String>> gameNamesList = games.stream()
                .map(game -> getAllGameNamesWithoutDecorations(game, sourceType))
                .collect(Collectors.toList());
        int totalGameNames = gameNamesList.stream().mapToInt(Set::size).sum();
        String formattedGameNamesCount = gameNamesList.stream()
                .map(gameNames -> String.format("%s", gameNames.size()))
                .collect(Collectors.joining(" - "));
        String formattedGameNames = gameNamesList.stream()
                .map(gameNames -> String.format("%s", gameNames))
                .collect(Collectors.joining(" - "));
        return String.format("gameIds: %s - %s total gameNames (%s): %s",
                gameIds, totalGameNames, formattedGameNamesCount, formattedGameNames);
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
        log.info("");
        log.info("****************************************");
        log.info("");
        log.info(String.format("** details for %s:", entry.getKey()));
        entry.getValue().forEach(RomOrganizer::logGameWithDifferentNames);
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

    private GameList createGameList() {
        List<String> gameListContent = loadGameListContent();
        return GameListParser.parseGameList(gameListContent, config.getRegionsPreferenceOrder());
    }

    private List<String> loadGameListContent() {
        Checker.checkArgument(Files.isRegularFile(this.gameListFile), "File '%s' does not exits", this.gameListFile);
        return FileUtils.readAllLines(gameListFile);
    }
}
