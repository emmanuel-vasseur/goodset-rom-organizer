package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
public class Game {
    private static final int UNKNOWN_ROM_GAME_ID = 0;
    private static final String NONGAME_ROM_NAME = "ZZZ(notgame):#NONGAME";

    private final int gameId;
    private final List<RomInfo> roms;
    private final RomGatheredType romGatheredType;
    private final GameNames gameListGameNames;
    private final GameNames romPathsGameNames;

    public double getGameNameReferenceDistributionRatio() {
        return gameListGameNames.getGameNameReference().getDistributionRatioInGame() *
                romPathsGameNames.getGameNameReference().getDistributionRatioInGame();
    }

    public boolean isRecognizedInGameList() {
        return !isNotRecognizedInGameList();
    }

    public boolean isNotRecognizedInGameList() {
        boolean unknownGame = gameId == UNKNOWN_ROM_GAME_ID;
        boolean nonGame = NONGAME_ROM_NAME.equals(gameListGameNames.getGameNameReference().getGameNameWithoutDecorations());
        return unknownGame || nonGame;
    }

    public boolean containOnlyOneRom() {
        return roms.size() == 1;
    }

    public Set<String> getRegions() {
        return getRoms().stream()
                .flatMap(rom -> rom.getRegions().stream())
                .collect(Collectors.toSet());
    }
}
