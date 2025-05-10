package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class GameName {
    private final String nameWithoutDecorations;
    private final List<String> allNamesWithDecorations;
    private final int totalNumberOfGameRoms;

    public static final Comparator<GameName> RATIO_COMPARATOR = Comparator.comparingDouble(GameName::getGameRatio).reversed();

    public double getGameRatio() {
        return allNamesWithDecorations.size() / (double) totalNumberOfGameRoms;
    }

}
