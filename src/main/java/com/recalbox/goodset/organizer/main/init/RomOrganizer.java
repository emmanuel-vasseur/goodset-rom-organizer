package com.recalbox.goodset.organizer.main.init;

import com.recalbox.goodset.organizer.GameListTransformer;
import com.recalbox.goodset.organizer.RomNameHandling;
import com.recalbox.goodset.organizer.config.ConfigProperties;
import com.recalbox.goodset.organizer.gamelist.GameList;
import com.recalbox.goodset.organizer.gamelist.GameListParser;
import com.recalbox.goodset.organizer.util.Checker;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.extern.java.Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Log
public class RomOrganizer {

    public final RomNameHandling romNameHandling = new RomNameHandling();
    public final ConfigProperties config = new ConfigProperties();

    public final Path romDirectory;
    public final Path gameListFile;

    public RomOrganizer(String romDirectory) {
        this.romDirectory = Paths.get(romDirectory).toAbsolutePath().normalize();
        this.gameListFile = this.romDirectory.resolve(config.getGameListFilename());

        Checker.checkArgument(Files.isDirectory(this.romDirectory), "Directory '%s' does not exits", this.romDirectory);
    }

    public GameListTransformer createGameListTransformer() {
        List<String> gameListContent = loadGameListContent();
        return new GameListTransformer(gameListContent, romNameHandling, config);
    }

    public GameList createGameList() {
        List<String> gameListContent = loadGameListContent();
        return GameListParser.parseGameList(gameListContent, config.getRegionsPreferenceOrder());
    }

    private List<String> loadGameListContent() {
        Checker.checkArgument(Files.isRegularFile(this.gameListFile), "File '%s' does not exits", this.gameListFile);
        return FileUtils.readAllLines(gameListFile);
    }
}
