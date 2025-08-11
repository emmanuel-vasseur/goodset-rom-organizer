package com.recalbox.goodset.organizer.main;

import com.recalbox.goodset.organizer.GameListTransformer;
import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;

@RequiredArgsConstructor
public class RenameRomVariationsInGameList {

    private final RomOrganizer romOrganizer;

    public static void main(String... args) {
        new RenameRomVariationsInGameList(createRomOrganizer(args))
                .renameRomVariationsInGameList();
    }

    public void renameRomVariationsInGameList() {
        GameListTransformer gameListTransformer = romOrganizer.createGameListTransformer();
        gameListTransformer.renameRomVariations();
        FileUtils.writeLinesIntoFile(gameListTransformer.getGameListContent(), romOrganizer.gameListFile);
    }
}
