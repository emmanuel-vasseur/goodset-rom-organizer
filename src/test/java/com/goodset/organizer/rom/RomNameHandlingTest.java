package com.goodset.organizer.rom;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RomNameHandlingTest {

    private RomNameHandling romNameHandling = new RomNameHandling();

    @Nested
    class ReplaceRomTypesTests {

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Crystal Warriors (UE).gg|Crystal Warriors (Europe, USA).gg",
                "James Bond 007 - The Duel (E) [!].gg|James Bond 007 - The Duel (Europe) [!].gg",
                "Jang Pung II (K).gg|Jang Pung II (Korea).gg",
                "XPMCK v15 - Scotland by mic_ (PD).gg|XPMCK v15 - Scotland by mic_ (Public Domain).gg",
                "Woody Pop (UEB) (V1.1) [!].gg|Woody Pop (World) (V1.1) [!].gg",
                "Ultimate Soccer (JEB) [!].gg|Ultimate Soccer (World) [!].gg",
                "Tails' Adventures (JU) [!].gg|Tails' Adventures (USA, Japan) [!].gg",
                "Surf Ninjas (UB) [!].gg|Surf Ninjas (USA, Brazil) [!].gg",
                "Super Monaco GP (JK).gg|Super Monaco GP (Japan, Korea).gg",
                "Crystal Warriors (UE).gg|Crystal Warriors (Europe, USA).gg",
                "Sonic The Hedgehog (JE) (V1.1) [!].gg|Sonic The Hedgehog (Europe, Japan) (V1.1) [!].gg",
                "Incredible Crash Dummies, The (JUE) [!].gg|Incredible Crash Dummies, The (World) [!].gg",
                "Iron Man X-O Manowar in Heavy Metal (U).gg|Iron Man X-O Manowar in Heavy Metal (USA).gg",

                "Iron Man X-O Manowar in Heavy Metal (U) [b1].gg|Iron Man X-O Manowar in Heavy Metal (USA) [BadDump 1].gg",
                "Iron Man X-O Manowar in Heavy Metal (U) [a1].gg|Iron Man X-O Manowar in Heavy Metal (USA) [Alternate 1].gg",
                "Indiana Jones and the Last Crusade (UE) [t1].gg|Indiana Jones and the Last Crusade (Europe, USA) [Training 1].gg",
                "Kinetic Connection (J) [o1].gg|Kinetic Connection (Japan) [OverDump 1].gg",
                "Zool (J) [hI].gg|Zool (Japan) [Hack I].gg",
                "Alex Kidd in Miracle World (T) [S].sms|Alex Kidd in Miracle World (Taiwan) [MasterSystem Mode].sms",
        })
        void replaceEachRomType(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceRomTypes(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @Test
        void replaceMultipleDifferentRomTypes() {
            String newRomName = romNameHandling.replaceRomTypes("Donald Duck no Yottsu no Hihou (J) [t2][hI].gg");
            assertThat(newRomName).isEqualTo("Donald Duck no Yottsu no Hihou (Japan) [Training 2][Hack I].gg");
        }

        @Test
        void shouldNotReplaceUnknownRomTypes() {
            String newRomName = romNameHandling.replaceRomTypes("FIFA International Soccer (M4) [!].gg");
            assertThat(newRomName).isEqualTo("FIFA International Soccer (M4) [!].gg");
        }

        @Test
        void replaceMultipleTimesSameRomType() {
            String newRomName = romNameHandling.replaceRomTypes("Jang Pung II [o1][o2].gg");
            assertThat(newRomName).isEqualTo("Jang Pung II [OverDump 1][OverDump 2].gg");
        }

    }

    @Nested
    class UnknownRomTypesTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "Crystal Warriors (UE).gg",
                "James Bond 007 - The Duel (E).gg",
                "Jang Pung II (K).gg",
                "XPMCK v15 - Scotland by mic_ (PD).gg",
                "Woody Pop (UEB).gg",
                "Ultimate Soccer (JEB).gg",
                "Tails' Adventures (JU).gg",
                "Surf Ninjas (UB).gg",
                "Super Monaco GP (JK).gg",
                "Crystal Warriors (UE).gg",
                "Sonic The Hedgehog (JE).gg",
                "Incredible Crash Dummies, The (JUE).gg",
                "Iron Man X-O Manowar in Heavy Metal (U).gg",
                "Iron Man X-O Manowar in Heavy Metal (U) [b1].gg",
                "Iron Man X-O Manowar in Heavy Metal (U) [a1].gg",
                "Indiana Jones and the Last Crusade (UE) [t1].gg",
                "Kinetic Connection (J) [o1].gg",
                "Zool (J) [hI].gg",
                "Jang Pung II [S].gg",
        })
        void shouldNotHaveUnknownRomTypes_WhenRomName_Contains_KnownRomTypeMapping(String romName) {
            boolean hasUnknownRomTypes = romNameHandling.hasUnknownRomTypes(romName);
            assertThat(hasUnknownRomTypes).isEqualTo(false);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "James Bond 007 - The Duel [!].gg",
                "Taz in Escape from Mars - Star Wars Text (Hack).gg",
                "Double Dragon (Prototype).gg",
                "Sonic & Tails (Demo).gg",
                "Sonic Drift (Sample).gg",
        })
        void shouldNotHaveUnknownRomTypes_WhenRomName_Contains_KnownNotReplacedRomTypes(String romName) {
            boolean hasUnknownRomTypes = romNameHandling.hasUnknownRomTypes(romName);
            assertThat(hasUnknownRomTypes).isEqualTo(false);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Woody Pop (V1.1).gg",
                "Sports Trivia (Prototype - Mar 09, 1995).gg",
                "Olympic Gold - Barcelona '92 (M8).gg",
                "Asterix and the Secret Mission (M3).gg",
                "Xaropinho (Mappy Hack).gg",
        })
        void shouldNotHaveUnknownRomTypes_WhenRomName_Contains_KnownNotRegexReplacedRomTypes(String romName) {
            boolean hasUnknownRomTypes = romNameHandling.hasUnknownRomTypes(romName);
            assertThat(hasUnknownRomTypes).isEqualTo(false);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Woody Pop (V1.1).gg",
                "Sports Trivia (Prototype-Mar 09, 1995).gg",
                "Olympic Gold - Barcelona '92 (M9).gg",
                "Asterix and the Secret Mission (M2).gg",
                "Xaropinho (Mappy-Hack).gg",
                "Zool (with Invinciblity).gg",
        })
        void shouldHaveUnknownRomTypes_WhenRomName_Contains_UnknownRomType(String romName) {
            boolean hasUnknownRomTypes = romNameHandling.hasUnknownRomTypes(romName);
            assertThat(hasUnknownRomTypes).isEqualTo(true);
        }

    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Crystal Warriors (UE).gg|Crystal Warriors",
            "Sonic The Hedgehog (JE) (V1.1) [!].gg|Sonic The Hedgehog",
            "Incredible Crash Dummies, The [!].gg|Incredible Crash Dummies, The",

            "Shadam Crusader - Harukanaru Oukoku(J) [!].gg|Shadam Crusader - Harukanaru Oukoku",
            "Iron Man X-O Manowar in Heavy Metal[a1].gg|Iron Man X-O Manowar in Heavy Metal",
            "Indiana Jones and the Last Crusade (Whatever) [t1].gg|Indiana Jones and the Last Crusade",
            "Kinetic Connection [Whatever] (Whatever).gg|Kinetic Connection",
    })
    void shouldGetGameOfRomName(String romName, String expectedGameName) {
        String gameName = romNameHandling.getGame(romName);
        assertThat(gameName).isEqualTo(expectedGameName);
    }

    @Nested
    class LowerQualityRoms {
        @Test
        void shouldNotHaveLowerQualityRoms_WhenGame_Contains_OnlyGoodDumpRoms() {
            List<String> romNames = Arrays.asList(
                    "Sonic The Hedgehog (JE) (V1.1) [!].gg",
                    "Sonic The Hedgehog (JU) (V1.0) [!].gg",
                    "Sonic The Hedgehog (Prototype) [!].gg"
            );
            List<String> lowerQualityRomNames = romNameHandling.getLowerQualityRomNames(romNames);
            assertThat(lowerQualityRomNames).isEmpty();
        }

        @Test
        void shouldNotHaveLowerQualityRoms_WhenGame_NotContain_GoodDumpRoms() {
            List<String> romNames = Arrays.asList(
                    "Super Golf (J).gg",
                    "Super Golf (U).gg"
            );
            List<String> lowerQualityRomNames = romNameHandling.getLowerQualityRomNames(romNames);
            assertThat(lowerQualityRomNames).isEmpty();
        }

        @Test
        void shouldHaveLowerQualityRoms_WhenGame_NotContain_OnlyGoodDumpRoms() {
            List<String> romNames = Arrays.asList(
                    "Sega Game Pack 4 in 1 (E) [!].gg",
                    "Sega Game Pack 4 in 1 (E) [b1].gg",
                    "Sega Game Pack 4 in 1 (Prototype).gg"
            );
            List<String> lowerQualityRomNames = romNameHandling.getLowerQualityRomNames(romNames);
            assertThat(lowerQualityRomNames).containsExactlyInAnyOrder(
                    "Sega Game Pack 4 in 1 (E) [b1].gg",
                    "Sega Game Pack 4 in 1 (Prototype).gg"
            );
        }
    }
}