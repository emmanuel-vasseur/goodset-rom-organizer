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

        assertThat(gameList.findConflictingGameNames()).isEmpty();
        assertThat(gameList.findConflictingGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findConflictingGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findGamesToMerge()).isEmpty();
        assertThat(gameList.findGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findGamesToMerge(GameNameType.ROMPATH)).isEmpty();

        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames()).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge()).isEmpty();
    }

    @Test
    void shouldHaveSimpleConflictingGameNames() {
        GameList gameList = createGameList(
                createGame(new RomInfo(1, "Rom Name 1.rom", "Rom Name 1", null)),
                createGame(new RomInfo(2, "Rom Name 1.rom", "Rom Name 1", null)),
                createGame(new RomInfo(3, "Rom Name 3.rom", "Rom Name 3", null)),
                createGame(new RomInfo(4, "Rom Name 3.rom", "Rom Name 3", null))
        );

        assertThat(gameList.findConflictingGameNames()).hasSize(2)
                .containsKeys("Rom Name 1", "Rom Name 3");
        assertThat(gameList.findConflictingGameNames(GameNameType.GAMELIST))
                .hasSize(2).containsKeys("Rom Name 1", "Rom Name 3");
        assertThat(gameList.findConflictingGameNames(GameNameType.ROMPATH))
                .hasSize(2).containsKeys("Rom Name 1", "Rom Name 3");
        assertThat(gameList.findGamesToMerge()).hasSize(2);
        assertThat(gameList.findGamesToMerge(GameNameType.GAMELIST)).hasSize(2);
        assertThat(gameList.findGamesToMerge(GameNameType.ROMPATH)).hasSize(2);

        assertThat(gameIds(gameList.findConflictingGameNames().values()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.findConflictingGameNames(GameNameType.GAMELIST).values()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.findConflictingGameNames(GameNameType.ROMPATH).values()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.findGamesToMerge()))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.findGamesToMerge(GameNameType.GAMELIST)))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));
        assertThat(gameIds(gameList.findGamesToMerge(GameNameType.ROMPATH)))
                .containsExactlyInAnyOrder(gameIds(1, 2), gameIds(3, 4));

        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames()).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge()).isEmpty();
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

        assertThat(gameList.findConflictingGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findConflictingGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findGamesToMerge(GameNameType.ROMPATH)).isEmpty();

        assertThat(gameList.findConflictingGameNames()).hasSize(1).containsKeys("Rom Name 2");
        assertThat(gameIds(gameList.findConflictingGameNames().values())).containsExactlyInAnyOrder(gameIds(1, 2));
        assertThat(gameList.findGamesToMerge()).hasSize(1);
        assertThat(gameIds(gameList.findGamesToMerge())).containsExactlyInAnyOrder(gameIds(1, 2));

        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames()).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge()).isEmpty();
    }

    @Test
    void shouldHaveLinkedConflictingGameNames() {
        GameList gameList = createGameList(
                createGame(
                        new RomInfo(0, "Rom Name 0.rom", "Rom Name 0", null),
                        new RomInfo(0, "Rom Name 0.rom", "Rom Name 0", null),
                        new RomInfo(0, "Rom Name 1.rom", "Rom Name 11", null),
                        new RomInfo(0, "Rom Name 19.rom", "Rom Name 9", null),
                        new RomInfo(0, "Rom Name 10.rom", "Rom Name 10", null)
                ),
                createGame(new RomInfo(1, "Rom Name 1.rom", "Rom Name 2", null)),
                createGame(new RomInfo(2, "Rom Name 2.rom", "Rom Name 3", null)),
                createGame(new RomInfo(3, "Rom Name 3.rom", "Rom Name 4", null)),
                createGame(new RomInfo(4, "Rom Name 4.rom", "Rom Name 5", null)),
                createGame(new RomInfo(5, "Rom Name 5.rom", "Rom Name 6", null)),
                createGame(new RomInfo(6, "Rom Name 6.rom", "Rom Name 7", null)),
                createGame(new RomInfo(7, "Rom Name 7.rom", "Rom Name 1", null)),
                createGame(new RomInfo(8, "Rom Name 8.rom", "Rom Name 9", null)),
                createGame(new RomInfo(9, "Rom Name 9.rom", "Rom Name 8", null)),
                createGame(new RomInfo(10, "Rom Name 10.rom", "Rom Name 10", null))
        );

        assertThat(gameList.findConflictingGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findConflictingGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameIds(gameList.findConflictingGameNames()))
                .hasSize(9)
                .containsEntry("Rom Name 1", gameIds(7, 1))
                .containsEntry("Rom Name 2", gameIds(1, 2))
                .containsEntry("Rom Name 3", gameIds(2, 3))
                .containsEntry("Rom Name 4", gameIds(3, 4))
                .containsEntry("Rom Name 5", gameIds(4, 5))
                .containsEntry("Rom Name 6", gameIds(5, 6))
                .containsEntry("Rom Name 7", gameIds(6, 7))
                .containsEntry("Rom Name 8", gameIds(8, 9))
                .containsEntry("Rom Name 9", gameIds(8, 9));

        assertThat(gameList.findGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findGamesToMerge(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameIds(gameList.findGamesToMerge()))
                .hasSize(2)
                .containsExactlyInAnyOrder(gameIds(1, 2, 3, 4, 5, 6, 7), gameIds(8, 9));

        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGameNames(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameIds(gameList.findPotentialProblematicGameNames()))
                .hasSize(7)
                .containsEntry("Rom Name 1", gameIds(7, 1))
                .containsEntry("Rom Name 2", gameIds(1, 2))
                .containsEntry("Rom Name 3", gameIds(2, 3))
                .containsEntry("Rom Name 4", gameIds(3, 4))
                .containsEntry("Rom Name 5", gameIds(4, 5))
                .containsEntry("Rom Name 6", gameIds(5, 6))
                .containsEntry("Rom Name 7", gameIds(6, 7));

        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameIds(gameList.findPotentialProblematicGamesToMerge()))
                .hasSize(1)
                .containsExactly(gameIds(1, 2, 3, 4, 5, 6, 7));

        assertThat(gameIds(gameList.findGamesHavingSameNamesWithoutDecoration(GameNameType.GAMELIST)))
                .hasSize(2)
                .containsEntry("Rom Name 9", gameIds(8, 0))
                .containsEntry("Rom Name 10", gameIds(10, 0));
        assertThat(gameIds(gameList.findGamesHavingSameNamesWithoutDecoration(GameNameType.ROMPATH)))
                .hasSize(2)
                .containsEntry("Rom Name 1", gameIds(1, 0))
                .containsEntry("Rom Name 10", gameIds(10, 0));
        assertThat(gameIds(gameList.findGamesHavingSameNamesWithoutDecoration()))
                .hasSize(10)
                .containsEntry("Rom Name 1", gameIds(7, 1, 0))
                .containsEntry("Rom Name 2", gameIds(1, 2))
                .containsEntry("Rom Name 3", gameIds(2, 3))
                .containsEntry("Rom Name 4", gameIds(3, 4))
                .containsEntry("Rom Name 5", gameIds(4, 5))
                .containsEntry("Rom Name 6", gameIds(5, 6))
                .containsEntry("Rom Name 7", gameIds(6, 7))
                .containsEntry("Rom Name 8", gameIds(8, 9))
                .containsEntry("Rom Name 9", gameIds(8, 9, 0))
                .containsEntry("Rom Name 10", gameIds(10, 0));

        assertThat(gameIds(gameList.findGamesToMerge2(GameNameType.GAMELIST)))
                .hasSize(2)
                .containsExactlyInAnyOrder(gameIds(8, 0), gameIds(10, 0));
        assertThat(gameIds(gameList.findGamesToMerge2(GameNameType.ROMPATH)))
                .hasSize(2)
                .containsExactlyInAnyOrder(gameIds(1, 0), gameIds(10, 0));
        assertThat(gameIds(gameList.findGamesToMerge2()))
                .hasSize(3)
                .containsExactlyInAnyOrder(gameIds(0, 1, 2, 3, 4, 5, 6, 7), gameIds(0, 8, 9), gameIds(10, 0));

        assertThat(gameList.findPotentialProblematicGamesHavingSameNamesWithoutDecoration(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesHavingSameNamesWithoutDecoration(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameIds(gameList.findPotentialProblematicGamesHavingSameNamesWithoutDecoration()))
                .hasSize(8)
                .containsEntry("Rom Name 1", gameIds(7, 1, 0))
                .containsEntry("Rom Name 2", gameIds(1, 2))
                .containsEntry("Rom Name 3", gameIds(2, 3))
                .containsEntry("Rom Name 4", gameIds(3, 4))
                .containsEntry("Rom Name 5", gameIds(4, 5))
                .containsEntry("Rom Name 6", gameIds(5, 6))
                .containsEntry("Rom Name 7", gameIds(6, 7))
                .containsEntry("Rom Name 8", gameIds(8, 9));

        assertThat(gameList.findPotentialProblematicGamesToMerge2(GameNameType.GAMELIST)).isEmpty();
        assertThat(gameList.findPotentialProblematicGamesToMerge2(GameNameType.ROMPATH)).isEmpty();
        assertThat(gameIds(gameList.findPotentialProblematicGamesToMerge2()))
                .hasSize(1)
                .containsExactly(gameIds(0, 1, 2, 3, 4, 5, 6, 7));
    }

    private static Map<String, Set<Integer>> gameIds(Map<String, Set<Game>> gamesMap) {
        return gamesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(Game::getGameId).collect(Collectors.toSet())
                ));
    }

    private static List<Set<Integer>> gameIds(Collection<Set<Game>> gamesCollection) {
        return gamesCollection.stream()
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
