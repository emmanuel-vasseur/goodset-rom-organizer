package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class GameName {

    private final String gameNameWithoutDecorations;
    private final GameNameType type;
    private final List<RomInfo> gameNameRoms;
    private final int totalNumberOfGameRoms;

    public static final Comparator<GameName> RATIO_COMPARATOR = Comparator.comparingDouble(GameName::getGameRatio).reversed();

    public double getGameRatio() {
        return gameNameRoms.size() / (double) totalNumberOfGameRoms;
    }

    public List<String> getAllNamesWithDecorations() {
        return gameNameRoms.stream()
                .map(type.romNameExtractor)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getGameCountries() {
        return gameNameRoms.stream()
                .flatMap(rom -> rom.getRegions().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
