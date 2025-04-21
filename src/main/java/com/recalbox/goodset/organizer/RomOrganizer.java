package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.ConfigProperties;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.extern.java.Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log
public class RomOrganizer {

    private final RomNameHandling romNameHandling = new RomNameHandling();
    private final ConfigProperties config = new ConfigProperties();

    private final Path romDirectory;
    private final Path gameListFile;

    public RomOrganizer(String romDirectory) {
        this.romDirectory = Paths.get(romDirectory).toAbsolutePath().normalize();
        this.gameListFile = this.romDirectory.resolve(config.getGameListFilename());

        checkArgument(Files.isDirectory(this.romDirectory), "Directory '%s' does not exits", this.romDirectory);
    }

    public void listUnknownRomTypesInFilenames() {
        List<Path> romFiles = FileUtils.listFiles(romDirectory);
        Map<String, List<String>> romNamesWithUnknownTypes = romFiles.stream()
                .collect(Collectors.toMap(
                        rom -> rom.getFileName().toString(),
                        rom -> romNameHandling.getFilenameUnknownRomTypes(rom.getFileName().toString())));
        romNamesWithUnknownTypes.values()
                .removeIf(List::isEmpty);

        if (romNamesWithUnknownTypes.isEmpty()) {
            log.info(String.format("** Result OK, No unknown rom types found in '%s' files **", romDirectory));
        } else {
            log.warning(String.format("******** Unknown rom types found in '%s' files ********", romDirectory));
            romNamesWithUnknownTypes.forEach((romName, unknownRomTypes) ->
                    log.warning(String.format("%s : %s", romName, String.join(", ", unknownRomTypes))
            ));
        }
    }

    public void renameFilenameRomTypes() {
        List<Path> romFiles = FileUtils.listFiles(romDirectory);
        romFiles.forEach(this::renameRom);
    }

    private void renameRom(Path rom) {
        String newName = romNameHandling.replaceFilenameRomTypes(rom.getFileName().toString());
        if (!rom.getFileName().toString().equals(newName)) {
            FileUtils.renameFileName(rom, newName);
        }
    }

    public void groupRomsByGame() {
        List<Path> romFiles = FileUtils.listFiles(romDirectory);
        romFiles.forEach(this::moveRomToGameDirectory);

        List<Path> gameDirectories = FileUtils.listDirectories(romDirectory);
        gameDirectories.forEach(this::splitGoodAndNotGoodRoms);
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
        checkArgument(Files.isRegularFile(this.gameListFile), "File '%s' does not exits", this.gameListFile);
        List<String> gameListContent = FileUtils.readAllLines(gameListFile);
        GameListTransformer gameListTransformer = new GameListTransformer(gameListContent, config);

        List<String> folderImagesToDelete = gameListTransformer.getFolderImagesThatWillBeReplaced();
        gameListTransformer.changeFolderImagesByRomImages();

        FileUtils.writeLinesIntoFile(gameListTransformer.getGameListContent(), gameListFile);
        folderImagesToDelete.forEach(this::deleteFileAndAllEmptyParentDirectories);
    }

    private void deleteFileAndAllEmptyParentDirectories(String folderImage) {
        Path folderImageFile = romDirectory.resolve(folderImage);
        FileUtils.deleteFileAndAllEmptyParentDirectories(folderImageFile);
    }

    private static void checkArgument(boolean condition, String exceptionMessage, Object... messageArgs) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(exceptionMessage, messageArgs));
        }
    }
}
