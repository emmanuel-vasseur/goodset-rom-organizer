package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Getter
public class GameList {
    private final List<Game> games;

    public Game getGame(int gameId) {
        return games.stream()
                .filter(game -> game.getGameId() == gameId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Game with id " + gameId + " not found."));
    }
}
