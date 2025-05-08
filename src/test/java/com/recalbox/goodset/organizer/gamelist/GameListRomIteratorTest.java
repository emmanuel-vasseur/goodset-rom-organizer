package com.recalbox.goodset.organizer.gamelist;

import com.recalbox.goodset.organizer.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GameListRomIteratorTest {

    @Test
    void shouldNextIterateOverGamesWithoutHasNext() {
        List<String> gameListLines = TestUtils.loadGameListLines("/multiple-roms-of-one-game.gamelist.xml");
        GameListRomIterator gameListRomIterator = new GameListRomIterator(gameListLines);

        RomInfo firstRom = gameListRomIterator.next();
        RomInfo secondRom = gameListRomIterator.next();

        Assertions.assertThatThrownBy(gameListRomIterator::next)
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No more element to iterate over.");
        assertThat(firstRom).usingRecursiveAssertion().isNotEqualTo(secondRom);
    }

    @Test
    void shouldHasNextDoesNotIterateOverGames() {
        List<String> gameListLines = TestUtils.loadGameListLines("/multiple-roms-of-one-game.gamelist.xml");
        GameListRomIterator gameListRomIterator = new GameListRomIterator(gameListLines);

        assertThat(gameListRomIterator.hasNext()).isTrue();
        assertThat(gameListRomIterator.hasNext()).isTrue();
        assertThat(gameListRomIterator.hasNext()).isTrue();
        gameListRomIterator.next();
        assertThat(gameListRomIterator.hasNext()).isTrue();
        assertThat(gameListRomIterator.hasNext()).isTrue();
        assertThat(gameListRomIterator.hasNext()).isTrue();
        gameListRomIterator.next();
        assertThat(gameListRomIterator.hasNext()).isFalse();
        assertThat(gameListRomIterator.hasNext()).isFalse();
    }

    @Test
    void shouldIterateOverGamesEvenIfEndTagsDoesNotExists() {
        List<String> gameListLines = TestUtils.loadGameListLines("/multiple-roms-of-one-game.gamelist.xml")
                .stream()
                .filter(line -> !line.matches("\\s*</\\w+>\\s*"))
                .collect(Collectors.toList());
        GameListRomIterator gameListRomIterator = new GameListRomIterator(gameListLines);

        assertThat(gameListRomIterator.hasNext()).isTrue();
        RomInfo uniqueRom = gameListRomIterator.next();
        assertThat(gameListRomIterator.hasNext()).isFalse();
        assertThat(uniqueRom.getPath()).isNotBlank();
        assertThat(uniqueRom.getName()).isNotBlank();
        assertThat(uniqueRom.getImage()).isNotBlank();
    }
}
