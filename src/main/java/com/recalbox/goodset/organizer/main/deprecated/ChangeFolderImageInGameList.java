package com.recalbox.goodset.organizer.main.deprecated;

import com.recalbox.goodset.organizer.GameListTransformer;
import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;

@RequiredArgsConstructor
public class ChangeFolderImageInGameList {

    private final RomOrganizer romOrganizer;

    public static void main(String... args) {
        new ChangeFolderImageInGameList(createRomOrganizer(args))
                .changeFolderImageInGameList();
    }

    public void changeFolderImageInGameList() {
        GameListTransformer gameListTransformer = romOrganizer.createGameListTransformer();

        List<String> folderImagesToDelete = gameListTransformer.getFolderImagesThatWillBeReplaced();
        gameListTransformer.changeFolderImagesByRomImages();

        FileUtils.writeLinesIntoFile(gameListTransformer.getGameListContent(), romOrganizer.gameListFile);
        folderImagesToDelete.forEach(this::deleteFileAndAllEmptyParentDirectories);
    }

    private void deleteFileAndAllEmptyParentDirectories(String folderImage) {
        Path folderImageFile = romOrganizer.romDirectory.resolve(folderImage);
        FileUtils.deleteFileAndAllEmptyParentDirectories(folderImageFile);
    }
}
