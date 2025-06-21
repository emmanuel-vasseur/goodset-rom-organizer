package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class GameList {
    private static final Comparator<Game> NON_OBVIOUS_GAME_REFERENCES_COMPARATOR = Comparator.<Game, Boolean>
                    comparing(game -> game.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio())
            .thenComparing(game -> game.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio())
            .thenComparing(Game::getGameNameReferenceDistributionRatio);

    private final List<Game> games;

    public Game getGame(int gameId) {
        return games.stream()
                .filter(game -> game.getGameId() == gameId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Game with id " + gameId + " not found."));
    }

    public List<Game> getGamesSortedByNonObviousGameReferences() {
        return games.stream()
                .sorted(NON_OBVIOUS_GAME_REFERENCES_COMPARATOR)
                .collect(Collectors.toList());
    }
}
