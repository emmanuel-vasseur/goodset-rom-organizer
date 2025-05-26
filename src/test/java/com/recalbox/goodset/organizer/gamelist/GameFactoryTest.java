package com.recalbox.goodset.organizer.gamelist;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.recalbox.goodset.organizer.gamelist.RomGatheredType.*;
import static org.assertj.core.api.Assertions.assertThat;

class GameFactoryTest {

    @Test
    void shouldExtractRomCountry() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name (EU) (US).rom", "Rom Name [EU,US]", null),
                new RomInfo(123, "Rom Name (JP) [!].rom", "Rom Name [JP] (Best)", null)
        ));

        assertThat(game.getRoms().get(0).getRegions()).containsExactlyInAnyOrder("Europe", "America");
        assertThat(game.getRoms().get(1).getRegions()).containsExactlyInAnyOrder("Asia");
        assertThat(game.getRegions()).containsExactlyInAnyOrder("Europe", "America", "Asia");
        assertThat(game.getRegions()).containsExactlyInAnyOrder("Europe", "America", "Asia");
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueAndSame() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name (EU) (US).rom", "Rom Name [EU,US]", null),
                new RomInfo(123, "Rom Name (JP) [!].rom", "Rom Name [JP] (Best)", null)
        ));

        assertThat(game.getRomGatheredType()).isEqualTo(SAME_UNIQUE_NAME_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameGatheredRatio()).isEqualTo(1.0);
        assertThat(game.getRegions()).containsExactlyInAnyOrder("Europe", "America", "Asia");

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getNamesWithoutDecorationsFromRomPaths();
        assertThat(namesWithoutDecorationsFromRomPaths).hasSize(1);
        GameName romPathGameName = namesWithoutDecorationsFromRomPaths.get(0);
        assertThat(romPathGameName.getGameNameWithoutDecorations()).isEqualTo("Rom Name");
        assertThat(romPathGameName.getAllNamesWithDecorations())
                .containsExactlyInAnyOrder("Rom Name (EU) (US).rom", "Rom Name (JP) [!].rom");
        assertThat(romPathGameName.getTotalNumberOfGameRoms()).isEqualTo(2);
        assertThat(romPathGameName.getGameRatio()).isEqualTo(1.0);
        assertThat(romPathGameName.getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);

        List<GameName> namesWithoutDecorationsFromGameList = game.getNamesWithoutDecorationsFromGameList();
        assertThat(namesWithoutDecorationsFromGameList).hasSize(1);
        GameName gameListGameName = namesWithoutDecorationsFromGameList.get(0);
        assertThat(gameListGameName.getGameNameWithoutDecorations()).isEqualTo("Rom Name");
        assertThat(gameListGameName.getAllNamesWithDecorations())
                .containsExactlyInAnyOrder("Rom Name [EU,US]", "Rom Name [JP] (Best)");
        assertThat(gameListGameName.getTotalNumberOfGameRoms()).isEqualTo(2);
        assertThat(gameListGameName.getGameRatio()).isEqualTo(1.0);
        assertThat(gameListGameName.getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueButDifferent() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 2 [EU,US]", null),
                new RomInfo(123, "Rom Name 1 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null)
        ));

        assertThat(game.getRomGatheredType()).isEqualTo(DIFFERENT_UNIQUE_NAME_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameGatheredRatio()).isEqualTo(1.0);

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getNamesWithoutDecorationsFromRomPaths();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1");
        assertThat(namesWithoutDecorationsFromRomPaths.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);

        List<GameName> namesWithoutDecorationsFromGameList = game.getNamesWithoutDecorationsFromGameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 2");
        assertThat(namesWithoutDecorationsFromGameList.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreSameButMultiples() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 2 [EU,US]", null),
                new RomInfo(123, "Rom Name 1 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null),
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null)
        ));

        assertThat(game.getRomGatheredType()).isEqualTo(SAME_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameGatheredRatio()).isEqualTo(0.5625); // 0.75 * 0.75

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getNamesWithoutDecorationsFromRomPaths();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameRatio)
                .containsExactly(0.75, 0.25);
        assertThat(namesWithoutDecorationsFromRomPaths.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 2L)
                .containsEntry("America", 2L)
                .containsEntry("Asia", 1L);
        assertThat(namesWithoutDecorationsFromRomPaths.get(1).getGameCountries()).hasSize(1)
                .containsEntry("Asia", 1L);

        List<GameName> namesWithoutDecorationsFromGameList = game.getNamesWithoutDecorationsFromGameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 2", "Rom Name 1");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameRatio)
                .containsExactly(0.75, 0.25);
        assertThat(namesWithoutDecorationsFromGameList.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 2L);
        assertThat(namesWithoutDecorationsFromGameList.get(1).getGameCountries()).hasSize(2)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueOnlyInGameList() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 1 [JP] (Best)", null)
        ));

        assertThat(game.getRomGatheredType()).isEqualTo(UNIQUE_NAME_ONLY_IN_GAMELIST);
        assertThat(game.getGameNameGatheredRatio()).isEqualTo(0.5); // 1 * 0.5

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getNamesWithoutDecorationsFromRomPaths();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameRatio)
                .containsExactly(0.5, 0.5);

        List<GameName> namesWithoutDecorationsFromGameList = game.getNamesWithoutDecorationsFromGameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameRatio)
                .containsExactly(1.0);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueOnlyInPaths() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 1 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null)
        ));

        assertThat(game.getRomGatheredType()).isEqualTo(UNIQUE_NAME_ONLY_IN_PATHS);
        assertThat(game.getGameNameGatheredRatio()).isEqualTo(0.5); // 1 * 0.5

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getNamesWithoutDecorationsFromRomPaths();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameRatio)
                .containsExactly(1.0);

        List<GameName> namesWithoutDecorationsFromGameList = game.getNamesWithoutDecorationsFromGameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameRatio)
                .containsExactly(0.5, 0.5);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreDifferentAndMultiples() {
        Game game = GameFactory.create(123, Arrays.asList(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 3 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 4 [JP] (Best)", null)
        ));

        assertThat(game.getRomGatheredType()).isEqualTo(DIFFERENT_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameGatheredRatio()).isEqualTo(0.25); // 0.5 * 0.5

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getNamesWithoutDecorationsFromRomPaths();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameRatio)
                .containsExactly(0.5, 0.5);

        List<GameName> namesWithoutDecorationsFromGameList = game.getNamesWithoutDecorationsFromGameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 3", "Rom Name 4");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameRatio)
                .containsExactly(0.5, 0.5);
    }

}