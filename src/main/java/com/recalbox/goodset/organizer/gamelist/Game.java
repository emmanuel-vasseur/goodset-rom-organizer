package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

import static lombok.AccessLevel.PACKAGE;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
public class Game {
    private final int gameId;
    private final List<RomInfo> roms;
    private final RomGatheredType romGatheredType;
    private final List<GameName> namesWithoutDecorationsFromGameList;
    private final List<GameName> namesWithoutDecorationsFromRomPaths;

    public static final Comparator<Game> GAME_NAME_GATHERED_RATIO_COMPARATOR =
            Comparator.comparingDouble(Game::getGameNameGatheredRatio).reversed();

    public double getGameNameGatheredRatio() {
        return namesWithoutDecorationsFromGameList.get(0).getGameRatio() *
                namesWithoutDecorationsFromRomPaths.get(0).getGameRatio();
    }

    public boolean isRecognizedInGameList() {
        return gameId != 0;
    }

    public boolean containOnlyOneRom() {
        return roms.size() == 1;
    }

}
