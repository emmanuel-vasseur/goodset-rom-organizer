package com.recalbox.goodset.organizer.gamelist;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum GameNameType {
    GAMELIST(
            "gamelist",
            RomInfo::getName,
            RomInfo::getNameWithoutDecorations,
            Game::getGameListGameNames),
    ROMPATH(
            "filesystem",
            RomInfo::getFileName,
            RomInfo::getFileNameWithoutDecorations,
            Game::getRomPathsGameNames);

    public final String sourceType;
    public final Function<RomInfo, String> romNameExtractor;
    public final Function<RomInfo, String> romNameWithoutDecorationsExtractor;
    public final Function<Game, GameNames> gameNamesExtractor;
}
