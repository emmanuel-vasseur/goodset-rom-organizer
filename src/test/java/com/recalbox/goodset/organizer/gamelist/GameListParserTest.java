package com.recalbox.goodset.organizer.gamelist;

import com.recalbox.goodset.organizer.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class GameListParserTest {

    @Test
    void shouldGroupRomByGames() {
        List<String> gameListLines = TestUtils.loadGameListLines("/multiple-games-with-unknown-one-or-multiple-roms.gamelist.xml");
        GameList games = GameListParser.parseGameList(gameListLines, emptyList());
        assertThat(games.getGames()).hasSize(6);
        assertThat(games.getGames()).extracting(Game::getGameId).containsExactlyInAnyOrder(0, 46279, 12885, 65246, 151193, 151194);
    }

    @Test
    void shouldParseRomInfo() {
        List<String> gameListLines = TestUtils.loadGameListLines("/multiple-games-with-unknown-one-or-multiple-roms.gamelist.xml");
        GameList games = GameListParser.parseGameList(gameListLines, emptyList());

        assertThat(games.getGame(0).containOnlyOneRom()).isTrue();
        RomInfo game0Rom = games.getGame(0).getRoms().get(0);
        assertThat(game0Rom.getGameId()).isZero();
        assertThat(game0Rom.getPath()).isEqualTo("./Alex Kidd in Miracle World/Alex Kidd in Miracle World (Taiwan) [MasterSystem Mode].sms");
        assertThat(game0Rom.getName()).isEqualTo("Alex Kidd in Miracle World (Taiwan) [MasterSystem Mode]");
        assertThat(game0Rom.getImage()).isNull();

        assertThat(games.getGame(46279).containOnlyOneRom()).isTrue();
        RomInfo game46279Rom = games.getGame(46279).getRoms().get(0);
        assertThat(game46279Rom.getGameId()).isEqualTo(46279);
        assertThat(game46279Rom.getPath()).isEqualTo("./5 in 1 Funpak/5 in 1 Funpak (USA) [!].gg");
        assertThat(game46279Rom.getName()).isEqualTo("5 in 1 Funpak (USA) [!] [US]");
        assertThat(game46279Rom.getImage()).isEqualTo("./media/images/5 in 1 Funpak/5 in 1 Funpak (USA) [!].png");

        assertThat(games.getGame(12885).containOnlyOneRom()).isFalse();
        assertThat(games.getGame(12885).getRoms()).extracting(RomInfo::getPath)
                .containsExactlyInAnyOrder("./Alien 3/Alien 3 (Japan) [!].gg", "./Alien 3/Alien 3 (Europe, USA) [!].gg");
    }

    @Test
    void shouldNotRecognizeUnknownAndNonGameRoms() {
        List<String> gameListLines = TestUtils.loadGameListLines("/multiple-games-with-unknown-one-or-multiple-roms.gamelist.xml");
        GameList games = GameListParser.parseGameList(gameListLines, emptyList());

        assertThat(games.getGame(0).isRecognizedInGameList()).isFalse();
        assertThat(games.getGame(0).isNotRecognizedInGameList()).isTrue();
        assertThat(games.getGame(151193).isRecognizedInGameList()).isFalse();
        assertThat(games.getGame(151193).isNotRecognizedInGameList()).isTrue();
        assertThat(games.getGame(151194).isRecognizedInGameList()).isFalse();
        assertThat(games.getGame(151194).isNotRecognizedInGameList()).isTrue();

        assertThat(games.getGame(12885).isRecognizedInGameList()).isTrue();
        assertThat(games.getGame(12885).isNotRecognizedInGameList()).isFalse();
        assertThat(games.getGame(46279).isRecognizedInGameList()).isTrue();
        assertThat(games.getGame(46279).isNotRecognizedInGameList()).isFalse();
        assertThat(games.getGame(65246).isRecognizedInGameList()).isTrue();
        assertThat(games.getGame(65246).isNotRecognizedInGameList()).isFalse();
    }

}
