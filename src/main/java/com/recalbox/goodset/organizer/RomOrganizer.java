package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.ConfigProperties;
import com.recalbox.goodset.organizer.gamelist.*;
import com.recalbox.goodset.organizer.util.Checker;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

    public void listUnknownRomTypesInFilenames() {
        Stream<String> romNames = FileUtils.listFiles(romDirectory).stream().map(rom -> rom.getFileName().toString());
        Map<String, List<String>> romNamesWithUnknownTypes = romNameHandling.getRomNamesWithUnknownRomTypes(romNames, romNameHandling::getFilenameUnknownRomTypes);
        logRomNamesWithUnknownRomTypes(romNamesWithUnknownTypes, romDirectory);
    }

    public void listUnknownRomTypesInGamelist() {
        GameListTransformer gameListTransformer = createGameListTransformer();
        Map<String, List<String>> romNamesWithUnknownTypes = gameListTransformer.getGamelistRomNamesWithUnknownRomTypes();
        logRomNamesWithUnknownRomTypes(romNamesWithUnknownTypes, gameListFile);
    }

    private void logRomNamesWithUnknownRomTypes(Map<String, List<String>> romNamesWithUnknownTypes, Path location) {
        logMapOfList(romNamesWithUnknownTypes, location, "roms with unknown rom types", "unknown types");
        Map<String, List<String>> unknownRomTypesWithRomNames = inverseMapOfList(romNamesWithUnknownTypes);
        logMapOfList(unknownRomTypesWithRomNames, location, "unknown rom types", "roms");
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

    public void renameRomTypesInFilenames() {
        FileUtils.listFiles(romDirectory)
                .forEach(this::renameRom);
    }

    private void renameRom(Path rom) {
        String newName = romNameHandling.replaceFilenameRomTypes(rom.getFileName().toString());
        if (!rom.getFileName().toString().equals(newName)) {
            FileUtils.renameFileName(rom, newName);
        }
    }

    public void renameRomTypesInGamelist() {
        GameListTransformer gameListTransformer = createGameListTransformer();
        gameListTransformer.renameRomTypes();
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

    public void listGamesWithDifferentNames() {
        GameList gameList = createGameList();

        gameList.getGames().stream()
                .filter(game -> !game.isRecognizedInGameList())
                .forEach(game -> {
                    int romNumber = game.getRoms().size();
                    int nameNumber = game.getNamesWithoutDecorationsFromRomPaths().size();
                    log.info(String.format("%s different names (%s roms) in filesystem without gamelist information", nameNumber, romNumber));
                });

        Map<RomGatheredType, List<Game>> gamesWithOnlyOneRom = gameList.getGames().stream()
                .filter(Game::isRecognizedInGameList)
                .filter(Game::containOnlyOneRom)
                .collect(Collectors.groupingBy(Game::getRomGatheredType));
        Map<RomGatheredType, List<Game>> gamesWithMultipleRoms = gameList.getGames().stream()
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

    private static void logGamesWithDifferentNames(Map.Entry<RomGatheredType, List<Game>> entry) {
        log.info("");
        log.info("****************************************");
        log.info("");
        log.info(String.format("** details for %s:", entry.getKey()));
        entry.getValue().stream()
                .sorted(Game.GAME_NAME_GATHERED_RATIO_COMPARATOR)
                .forEach(RomOrganizer::logGameWithDifferentNames);
    }

    private static void logGameWithDifferentNames(Game game) {
        log.info(String.format("gameId %s", game.getGameId()));
        logDifferentNames(game.getNamesWithoutDecorationsFromGameList(), "gamelist");
        logDifferentNames(game.getNamesWithoutDecorationsFromRomPaths(), "filesystem");
    }

    private static void logDifferentNames(List<GameName> gameNames, String sourceType) {
        String formattedGameNames = gameNames.stream()
                .map(gameName -> String.format("%s (%s roms, %.2f%%)",
                        gameName.getNameWithoutDecorations(), gameName.getAllNamesWithDecorations().size(), gameName.getGameRatio() * 100))
                .collect(Collectors.joining(", "));
        log.info(String.format("- %s names in %s: %s", gameNames.size(), sourceType, formattedGameNames));
    }

    private GameList createGameList() {
        List<String> gameListContent = loadGameListContent();
        return GameListParser.parseGameList(gameListContent);
    }

    private List<String> loadGameListContent() {
        Checker.checkArgument(Files.isRegularFile(this.gameListFile), "File '%s' does not exits", this.gameListFile);
        return FileUtils.readAllLines(gameListFile);
    }
}
