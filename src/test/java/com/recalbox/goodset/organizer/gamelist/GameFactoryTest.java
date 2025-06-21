package com.recalbox.goodset.organizer.gamelist;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.recalbox.goodset.organizer.gamelist.RomGatheredType.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class GameFactoryTest {

    @Test
    void shouldExtractRomCountry() {
        Game game = createGame(
                new RomInfo(123, "Rom Name (EU) (US).rom", "Rom Name [EU,US]", null),
                new RomInfo(123, "Rom Name (JP) [!].rom", "Rom Name [JP] (Best)", null)
        );

        assertThat(game.getRoms().get(0).getRegions()).containsExactlyInAnyOrder("Europe", "America");
        assertThat(game.getRoms().get(1).getRegions()).containsExactlyInAnyOrder("Asia");
        assertThat(game.getRegions()).containsExactlyInAnyOrder("Europe", "America", "Asia");
        assertThat(game.getRegions()).containsExactlyInAnyOrder("Europe", "America", "Asia");
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueAndSame() {
        Game game = createGame(
                new RomInfo(123, "Rom Name (EU) (US).rom", "Rom Name [EU,US]", null),
                new RomInfo(123, "Rom Name (JP) [!].rom", "Rom Name [JP] (Best)", null)
        );

        assertThat(game.getRomGatheredType()).isEqualTo(SAME_UNIQUE_NAME_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameReferenceDistributionRatio()).isEqualTo(1.0);
        assertThat(game.getRegions()).containsExactlyInAnyOrder("Europe", "America", "Asia");

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getRomPathsGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromRomPaths).hasSize(1);
        GameName romPathGameName = namesWithoutDecorationsFromRomPaths.get(0);
        assertThat(romPathGameName.getGameNameWithoutDecorations()).isEqualTo("Rom Name");
        assertThat(romPathGameName.getAllNamesWithDecorations())
                .containsExactlyInAnyOrder("Rom Name (EU) (US).rom", "Rom Name (JP) [!].rom");
        assertThat(romPathGameName.getTotalNumberOfGameRoms()).isEqualTo(2);
        assertThat(romPathGameName.getDistributionRatioInGame()).isEqualTo(1.0);
        assertThat(romPathGameName.getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);

        List<GameName> namesWithoutDecorationsFromGameList = game.getGameListGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromGameList).hasSize(1);
        GameName gameListGameName = namesWithoutDecorationsFromGameList.get(0);
        assertThat(gameListGameName.getGameNameWithoutDecorations()).isEqualTo("Rom Name");
        assertThat(gameListGameName.getAllNamesWithDecorations())
                .containsExactlyInAnyOrder("Rom Name [EU,US]", "Rom Name [JP] (Best)");
        assertThat(gameListGameName.getTotalNumberOfGameRoms()).isEqualTo(2);
        assertThat(gameListGameName.getDistributionRatioInGame()).isEqualTo(1.0);
        assertThat(gameListGameName.getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueButDifferent() {
        Game game = createGame(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 2 [EU,US]", null),
                new RomInfo(123, "Rom Name 1 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null)
        );

        assertThat(game.getRomGatheredType()).isEqualTo(DIFFERENT_UNIQUE_NAME_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameReferenceDistributionRatio()).isEqualTo(1.0);

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getRomPathsGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1");
        assertThat(namesWithoutDecorationsFromRomPaths.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);

        List<GameName> namesWithoutDecorationsFromGameList = game.getGameListGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 2");
        assertThat(namesWithoutDecorationsFromGameList.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 1L)
                .containsEntry("America", 1L)
                .containsEntry("Asia", 1L);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreSameButMultiples() {
        Game game = createGame(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 2 [EU,US]", null),
                new RomInfo(123, "Rom Name 1 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null),
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null)
        );

        assertThat(game.getRomGatheredType()).isEqualTo(SAME_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameReferenceDistributionRatio()).isEqualTo(0.5625); // 0.75 * 0.75

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getRomPathsGameNames().getGameNamesStartedByReferenceSortedByDistributionRatio();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getDistributionRatioInGame)
                .containsExactly(0.75, 0.25);
        assertThat(namesWithoutDecorationsFromRomPaths.get(0).getGameCountries()).hasSize(3)
                .containsEntry("Europe", 2L)
                .containsEntry("America", 2L)
                .containsEntry("Asia", 1L);
        assertThat(namesWithoutDecorationsFromRomPaths.get(1).getGameCountries()).hasSize(1)
                .containsEntry("Asia", 1L);

        List<GameName> namesWithoutDecorationsFromGameList = game.getGameListGameNames().getGameNamesStartedByReferenceSortedByDistributionRatio();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 2", "Rom Name 1");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getDistributionRatioInGame)
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
        Game game = createGame(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 1 [JP] (Best)", null)
        );

        assertThat(game.getRomGatheredType()).isEqualTo(UNIQUE_NAME_ONLY_IN_GAMELIST);
        assertThat(game.getGameNameReferenceDistributionRatio()).isEqualTo(0.5); // 1 * 0.5

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getRomPathsGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getDistributionRatioInGame)
                .containsExactlyInAnyOrder(0.5, 0.5);

        List<GameName> namesWithoutDecorationsFromGameList = game.getGameListGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getDistributionRatioInGame)
                .containsExactly(1.0);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreUniqueOnlyInPaths() {
        Game game = createGame(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 1 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null)
        );

        assertThat(game.getRomGatheredType()).isEqualTo(UNIQUE_NAME_ONLY_IN_PATHS);
        assertThat(game.getGameNameReferenceDistributionRatio()).isEqualTo(0.5); // 1 * 0.5

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getRomPathsGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactly("Rom Name 1");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getDistributionRatioInGame)
                .containsExactly(1.0);

        List<GameName> namesWithoutDecorationsFromGameList = game.getGameListGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getDistributionRatioInGame)
                .containsExactlyInAnyOrder(0.5, 0.5);
    }

    @Test
    void shouldComputeAllGameFields_WhenRomNamesWithoutDecoration_AreDifferentAndMultiples() {
        Game game = createGame(
                new RomInfo(123, "Rom Name 1 (EU) (US).rom", "Rom Name 3 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 4 [JP] (Best)", null)
        );

        assertThat(game.getRomGatheredType()).isEqualTo(DIFFERENT_MULTIPLE_NAMES_IN_GAMELIST_AND_PATHS);
        assertThat(game.getGameNameReferenceDistributionRatio()).isEqualTo(0.25); // 0.5 * 0.5

        List<GameName> namesWithoutDecorationsFromRomPaths = game.getRomPathsGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 1", "Rom Name 2");
        assertThat(namesWithoutDecorationsFromRomPaths).extracting(GameName::getDistributionRatioInGame)
                .containsExactlyInAnyOrder(0.5, 0.5);

        List<GameName> namesWithoutDecorationsFromGameList = game.getGameListGameNames().getGameNameList();
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getGameNameWithoutDecorations)
                .containsExactlyInAnyOrder("Rom Name 3", "Rom Name 4");
        assertThat(namesWithoutDecorationsFromGameList).extracting(GameName::getDistributionRatioInGame)
                .containsExactlyInAnyOrder(0.5, 0.5);
    }

    @Test
    void shouldComputeGameNameReference() {
        List<RomInfo> roms = Arrays.asList(
                new RomInfo(123, "Rom Name 1 (FR) (EU).rom", "Rom Name 1 [FR,EU]", null),
                new RomInfo(123, "Rom Name 1 (US) (EU).rom", "Rom Name 1 [EU,US]", null),
                new RomInfo(123, "Rom Name 2 (JP) [!].rom", "Rom Name 2 [JP] (Best)", null),
                new RomInfo(123, "Rom Name 3 (FR) (EU).rom", "Rom Name 3 [FR,EU]", null),
                new RomInfo(123, "Rom Name 3 (US) [!].rom", "Rom Name 4 [US] (Best)", null),
                new RomInfo(123, "Rom Name 3 (US).rom", "Rom Name 4 [US]", null)
        );

        Game game1 = createGame(roms, Arrays.asList("France", "Europe", "America", "Asia"));
        assertThat(game1.getRomPathsGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 1");
        assertThat(game1.getGameListGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 1");
        assertThat(game1.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio()).isFalse();
        assertThat(game1.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio()).isTrue();
        assertThat(game1.getGameNameReferenceDistributionRatio()).isEqualTo((1d / 3) * (1d / 3));

        Game game2 = createGame(roms, Collections.singletonList("France"));
        assertThat(game2.getRomPathsGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 3");
        assertThat(game2.getGameListGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 1");
        assertThat(game2.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio()).isTrue();
        assertThat(game2.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio()).isTrue();
        assertThat(game2.getGameNameReferenceDistributionRatio()).isEqualTo((1d / 2) * (1d / 3));

        Game game3 = createGame(roms, Collections.singletonList("Europe"));
        assertThat(game3.getRomPathsGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 1");
        assertThat(game3.getGameListGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 1");
        assertThat(game3.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio()).isFalse();
        assertThat(game3.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio()).isTrue();
        assertThat(game3.getGameNameReferenceDistributionRatio()).isEqualTo((1d / 3) * (1d / 3));

        Game game4 = createGame(roms, Collections.singletonList("America"));
        assertThat(game4.getRomPathsGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 3");
        assertThat(game4.getGameListGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 4");
        assertThat(game4.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio()).isTrue();
        assertThat(game4.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio()).isTrue();
        assertThat(game4.getGameNameReferenceDistributionRatio()).isEqualTo((1d / 2) * (1d / 3));

        Game game5 = createGame(roms, Collections.singletonList("Asia"));
        assertThat(game5.getRomPathsGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 2");
        assertThat(game5.getGameListGameNames().getGameNameReference().getGameNameWithoutDecorations()).isEqualTo("Rom Name 2");
        assertThat(game5.getRomPathsGameNames().gameNameReferenceHasHighestDistributionRatio()).isFalse();
        assertThat(game5.getGameListGameNames().gameNameReferenceHasHighestDistributionRatio()).isFalse();
        assertThat(game5.getGameNameReferenceDistributionRatio()).isEqualTo((1d / 6) * (1d / 6));

        GameList gameList = new GameList(Arrays.asList(game1, game2, game3, game4, game5));
        assertThat(gameList.getGamesSortedByNonObviousGameReferences()).containsExactly(
                game5, game1, game3, game2, game4
        );
    }

    private static Game createGame(RomInfo... roms) {
        return GameFactory.create(123, Arrays.asList(roms), emptyList());
    }

    private static Game createGame(List<RomInfo> roms, List<String> regionsPreferenceOrder) {
        return GameFactory.create(123, roms, regionsPreferenceOrder);
    }

}