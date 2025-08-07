package com.recalbox.goodset.organizer.gamelist;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class ConflictingGameNamesTest {

    @Test
    void shouldNotHaveConflictingGameNames() {
        GameList gameList = createGameList(
                createGame(new RomInfo(1, "Rom Name 1.rom", "Rom Name 1", null)),
                createGame(new RomInfo(2, "Rom Name 2.rom", "Rom Name 2", null)),
                createGame(new RomInfo(3, "Rom Name 3.rom", "Rom Name 3", null))
        );

        assertThat(gameList.getConflictingGameNames()).isEmpty();
        assertThat(gameList.getConflictingGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.getConflictingGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.getGamesToMerge()).isEmpty();
        assertThat(gameList.getGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.getGamesToMerge(GameNameType.ROMPATH)).isEmpty();
    }

    @Test
    void shouldHaveSimpleConflictingGameNames() {
        GameList gameList = createGameList(
                createGame(new RomInfo(1, "Rom Name 1.rom", "Rom Name 1", null)),
                createGame(new RomInfo(2, "Rom Name 1.rom", "Rom Name 1", null)),
                createGame(new RomInfo(3, "Rom Name 3.rom", "Rom Name 3", null)),
                createGame(new RomInfo(4, "Rom Name 3.rom", "Rom Name 3", null))
        );

        assertThat(gameList.getConflictingGameNames()).hasSize(2)
                .containsKeys("Rom Name 1", "Rom Name 3");
        assertThat(gameList.getConflictingGameNames(GameNameType.GAMELIST))
                .hasSize(2).containsKeys("Rom Name 1", "Rom Name 3");
        assertThat(gameList.getConflictingGameNames(GameNameType.ROMPATH))
                .hasSize(2).containsKeys("Rom Name 1", "Rom Name 3");
        assertThat(gameList.getGamesToMerge()).hasSize(2);
        assertThat(gameList.getGamesToMerge(GameNameType.GAMELIST)).hasSize(2);
        assertThat(gameList.getGamesToMerge(GameNameType.ROMPATH)).hasSize(2);

        assertThat(gameIds(gameList.getConflictingGameNames().values()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.getConflictingGameNames(GameNameType.GAMELIST).values()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.getConflictingGameNames(GameNameType.ROMPATH).values()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.getGamesToMerge()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.getGamesToMerge(GameNameType.GAMELIST)))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.getGamesToMerge(GameNameType.ROMPATH)))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
    }

    @Test
    void shouldHaveMixedConflictingGameNames() {
        GameList gameList = createGameList(
                createGame(
                        new RomInfo(1, "Rom Name 1.rom", "Rom Name 1", null),
                        new RomInfo(1, "Rom Name 1.rom", "Rom Name 2", null)
                ),
                createGame(
                        new RomInfo(2, "Rom Name 2.rom", "Rom Name 3", null),
                        new RomInfo(2, "Rom Name 3.rom", "Rom Name 3", null)
                )
        );

        assertThat(gameList.getConflictingGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.getConflictingGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.getGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.getGamesToMerge(GameNameType.ROMPATH)).isEmpty();

        assertThat(gameList.getConflictingGameNames()).hasSize(1).containsKeys("Rom Name 2");
        assertThat(gameIds(gameList.getConflictingGameNames().values())).containsExactlyInAnyOrder(gameIds(1, 2));
        assertThat(gameList.getGamesToMerge()).hasSize(1);
        assertThat(gameIds(gameList.getGamesToMerge())).containsExactlyInAnyOrder(gameIds(1, 2));
    }

    @Test
    void shouldHaveLinkedConflictingGameNames() {
        GameList gameList = createGameList(
                createGame(new RomInfo(1, "Rom Name 1.rom", "Rom Name 2", null)),
                createGame(new RomInfo(2, "Rom Name 2.rom", "Rom Name 3", null)),
                createGame(new RomInfo(3, "Rom Name 3.rom", "Rom Name 4", null)),
                createGame(new RomInfo(4, "Rom Name 4.rom", "Rom Name 5", null)),
                createGame(new RomInfo(5, "Rom Name 5.rom", "Rom Name 6", null)),
                createGame(new RomInfo(6, "Rom Name 6.rom", "Rom Name 7", null)),
                createGame(new RomInfo(7, "Rom Name 7.rom", "Rom Name 1", null)),
                createGame(new RomInfo(8, "Rom Name 8.rom", "Rom Name 9", null)),
                createGame(new RomInfo(9, "Rom Name 9.rom", "Rom Name 8", null)),
                createGame(new RomInfo(0, "Rom Name 0.rom", "Rom Name 0", null))
        );

        assertThat(gameList.getConflictingGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.getConflictingGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.getGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.getGamesToMerge(GameNameType.ROMPATH)).isEmpty();

        assertThat(gameList.getConflictingGameNames()).hasSize(9).doesNotContainKeys("Rom Name 0");
        assertThat(gameIds(new TreeMap<>(gameList.getConflictingGameNames()).values()))
                // use TreeMap to have values ordered by keys : Rom Name 1, Rom Name 2, ..., Rom Name 9
                .containsExactly(
                        gameIds(7, 1), gameIds(1, 2), gameIds(2, 3), gameIds(3, 4), gameIds(4, 5), gameIds(5, 6), gameIds(6, 7),
                        gameIds(8, 9), gameIds(8, 9));

        assertThat(gameList.getGamesToMerge()).hasSize(2);
        assertThat(gameIds(gameList.getGamesToMerge()))
                .containsExactlyInAnyOrder(gameIds(1, 2, 3, 4, 5, 6, 7), gameIds(8, 9));

    }

    private static List<Set<Integer>> gameIds(Collection<Set<Game>> conflictingGamesLists) {
        return conflictingGamesLists.stream()
                .map(games -> games.stream().map(Game::getGameId).collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    private static Set<Integer> gameIds(Integer... gameIds) {
        return new HashSet<>(Arrays.asList(gameIds));
    }

    private static Game createGame(RomInfo... roms) {
        return GameFactory.create(roms[0].getGameId(), Arrays.asList(roms), emptyList());
    }

    private static GameList createGameList(Game... games) {
        return new GameList(Arrays.asList(games));
    }
}
