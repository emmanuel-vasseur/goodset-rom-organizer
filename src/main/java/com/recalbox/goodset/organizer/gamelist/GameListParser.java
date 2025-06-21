package com.recalbox.goodset.organizer.gamelist;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class GameListParser {

    public GameList parseGameList(List<String> gameListLines, List<String> regionsPreferenceOrder) {
        List<Game> games = parseRomInfos(gameListLines).stream()
                .collect(Collectors.groupingBy(RomInfo::getGameId))
                .entrySet().stream()
                .map(romEntry -> GameFactory.create(romEntry.getKey(), romEntry.getValue(), regionsPreferenceOrder))
                .collect(Collectors.toList());
        return new GameList(games);
    }

    private List<RomInfo> parseRomInfos(List<String> gameListLines) {
        GameListRomIterator iterator = new GameListRomIterator(gameListLines);
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .collect(Collectors.toList());
    }

}
