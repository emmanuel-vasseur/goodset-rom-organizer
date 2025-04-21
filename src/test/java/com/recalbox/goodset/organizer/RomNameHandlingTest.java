package com.recalbox.goodset.organizer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RomNameHandlingTest {

    RomNameHandling romNameHandling = new RomNameHandling();

    @Nested
    class ReplaceRomTypesTests {

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Crystal Warriors (eu) (us).gg|Crystal Warriors (Europe, USA).gg",
                "James Bond 007 - The Duel (EU) [!].gg|James Bond 007 - The Duel (Europe) [!].gg",
                "Jang Pung II (KR).gg|Jang Pung II (Korea).gg",
                "XPMCK v15 - Scotland by mic_ (PD).gg|XPMCK v15 - Scotland by mic_ (Public Domain).gg",
                "Woody Pop (eU) (uS) (bR) (V1.1) [!].gg|Woody Pop (Europe, USA, Brazil) (V1.1) [!].gg",
                "Tails' Adventures (us) (jp) [!].gg|Tails' Adventures (USA, Japan) [!].gg",
                "Surf Ninjas (us) (br) [!].gg|Surf Ninjas (USA, Brazil) [!].gg",
                "Super Monaco GP (jp) (kr).gg|Super Monaco GP (Japan, Korea).gg",
                "Crystal Warriors (eu) (us).gg|Crystal Warriors (Europe, USA).gg",
                "Sonic The Hedgehog (eu) (jp) (V1.1) [!].gg|Sonic The Hedgehog (Europe, Japan) (V1.1) [!].gg",
                "Iron Man X-O Manowar in Heavy Metal (US).gg|Iron Man X-O Manowar in Heavy Metal (USA).gg",

                "Iron Man X-O Manowar in Heavy Metal (US) [b1].gg|Iron Man X-O Manowar in Heavy Metal (USA) [BadDump 1].gg",
                "Iron Man X-O Manowar in Heavy Metal (US) [a1].gg|Iron Man X-O Manowar in Heavy Metal (USA) [Alternate 1].gg",
                "Indiana Jones and the Last Crusade (eu) (us) [t1].gg|Indiana Jones and the Last Crusade (Europe, USA) [Training 1].gg",
                "Kinetic Connection (JP) [o1].gg|Kinetic Connection (Japan) [OverDump 1].gg",
                "Zool (JP) [hI].gg|Zool (Japan) [Hack I].gg",
                "Alex Kidd in Miracle World (TW) [S].sms|Alex Kidd in Miracle World (Taiwan) [MasterSystem Mode].sms",
        })
        void replaceEachGamelistRomType(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceGamelistRomTypes(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Crystal Warriors (UE).gg|Crystal Warriors (eu)(us).gg",
                "James Bond 007 - The Duel (E) [!].gg|James Bond 007 - The Duel (EU) [!].gg",
                "Jang Pung II (K).gg|Jang Pung II (KR).gg",
                "Woody Pop (UEB) (V1.1) [!].gg|Woody Pop (eU)(uS)(bR) (V1.1) [!].gg",
                "Ultimate Soccer (JEB) [!].gg|Ultimate Soccer (World) [!].gg",
                "Tails' Adventures (JU) [!].gg|Tails' Adventures (us)(jp) [!].gg",
                "Surf Ninjas (UB) [!].gg|Surf Ninjas (us)(br) [!].gg",
                "Super Monaco GP (JK).gg|Super Monaco GP (jp)(kr).gg",
                "Crystal Warriors (UE).gg|Crystal Warriors (eu)(us).gg",
                "Sonic The Hedgehog (JE) (V1.1) [!].gg|Sonic The Hedgehog (eu)(jp) (V1.1) [!].gg",
                "Incredible Crash Dummies, The (JUE) [!].gg|Incredible Crash Dummies, The (World) [!].gg",
                "Iron Man X-O Manowar in Heavy Metal (U).gg|Iron Man X-O Manowar in Heavy Metal (US).gg",
                "Alex Kidd in Miracle World (T) [S].sms|Alex Kidd in Miracle World (TW) [S].sms",

                "Ax Battler - A Legend of Golden Axe [T+Fre20060926_Rysley].gg|Ax Battler - A Legend of Golden Axe (Fr)[T+Fre20060926_Rysley].gg",
                "Crystal Warriors [T-Fre].gg|Crystal Warriors (Fr)[T-Fre].gg",
                "Crystal Warriors [T+Fre.99_Asmodeath].gg|Crystal Warriors (Fr)[T+Fre.99_Asmodeath].gg",
                "Shining Force Gaiden - Final Conflict [T-Eng].gg|Shining Force Gaiden - Final Conflict (Uk)[T-Eng].gg",
                "Phantasy Star Gaiden [T+Bra_CBT].gg|Phantasy Star Gaiden (Br)[T+Bra_CBT].gg",
                "Megaman [T+Ger1.00_Star-trans].gg|Megaman (De)[T+Ger1.00_Star-trans].gg",
                "Ax Battler - A Legend of Golden Axe [T+Spa100_pkt].gg|Ax Battler - A Legend of Golden Axe (Es)[T+Spa100_pkt].gg",
                "Shinobi II - The Silent Fury [T+Rusbeta3_Lupus].gg|Shinobi II - The Silent Fury (Ru)[T+Rusbeta3_Lupus].gg",
        })
        void replaceEachFilenameRomType(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceFilenameRomTypes(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Ax Battler - A Legend of Golden Axe (Fr) [T+Fre20060926_Rysley].gg|Ax Battler - A Legend of Golden Axe [French Translation - 20060926_Rysley].gg",
                "Crystal Warriors (Fr) [T-Fre].gg|Crystal Warriors [French Older Translation].gg",
                "Crystal Warriors (Fr) [T+Fre.99_Asmodeath].gg|Crystal Warriors [French Translation - .99_Asmodeath].gg",
                "Shining Force Gaiden - Final Conflict (Uk) [T-Eng].gg|Shining Force Gaiden - Final Conflict [English Older Translation].gg",
                "Phantasy Star Gaiden (Br) [T+Bra_CBT].gg|Phantasy Star Gaiden [Brazilian Translation - CBT].gg",
                "Megaman (De) [T+Ger1.00_Star-trans].gg|Megaman [German Translation - 1.00_Star-trans].gg",
                "Ax Battler - A Legend of Golden Axe (Es) [T+Spa100_pkt].gg|Ax Battler - A Legend of Golden Axe [Spanish Translation - 100_pkt].gg",
                "Shinobi II - The Silent Fury (Ru) [T+Rusbeta3_Lupus].gg|Shinobi II - The Silent Fury [Russian Translation - beta3_Lupus].gg",
        })
        void replaceAllRomTranslationType(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceGamelistRomTypes(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @Test
        void replaceMultipleDifferentRomTypes() {
            String newFilenameRomName = romNameHandling.replaceFilenameRomTypes("Ax Battler - A Legend of Golden Axe (UE) (V2.4) [T+Fre][a1].gg");
            assertThat(newFilenameRomName).isEqualTo("Ax Battler - A Legend of Golden Axe (eu)(us) (V2.4) (Fr)[T+Fre][a1].gg");

            String newGamelistRomName = romNameHandling.replaceGamelistRomTypes("Ax Battler - A Legend of Golden Axe (eu) (us) (V2.4) (Fr) [T+Fre][a1].gg");
            assertThat(newGamelistRomName).isEqualTo("Ax Battler - A Legend of Golden Axe (Europe, USA) (V2.4) [French Translation][Alternate 1].gg");
        }

        @Test
        void shouldNotReplaceUnknownRomTypes() {
            String newRomName = romNameHandling.replaceFilenameRomTypes("FIFA International Soccer (M4) [!].gg");
            assertThat(newRomName).isEqualTo("FIFA International Soccer (M4) [!].gg");
        }

        @Test
        void replaceMultipleTimesSameRomType() {
            String newRomName = romNameHandling.replaceGamelistRomTypes("Jang Pung II [o1] [o2] (Fr) [T-Fre] (Fr) [T+Fre].gg");
            assertThat(newRomName).isEqualTo("Jang Pung II [OverDump 1] [OverDump 2] [French Older Translation] [French Translation].gg");
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
            List<String> unknownRomTypes = romNameHandling.getFilenameUnknownRomTypes(romName);
            assertThat(unknownRomTypes).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Crystal Warriors [T-Fre].gg",
                "Crystal Warriors [T+Fre.99_Asmodeath].gg",
                "Phantasy Star Gaiden [T+Bra_CBT].gg",
                "Megaman [T+Ger1.00_Star-trans].gg",
                "Ax Battler - A Legend of Golden Axe [T+Spa100_pkt].gg",
                "Shinobi II - The Silent Fury [T+Rusbeta3_Lupus].gg",
        })
        void shouldNotHaveUnknownRomTypes_WhenRomName_Contains_KnownRomTranslationTypeMapping(String romName) {
            List<String> unknownRomTypes = romNameHandling.getFilenameUnknownRomTypes(romName);
            assertThat(unknownRomTypes).isEmpty();
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
            List<String> unknownRomTypes = romNameHandling.getFilenameUnknownRomTypes(romName);
            assertThat(unknownRomTypes).isEmpty();
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
            List<String> unknownRomTypes = romNameHandling.getFilenameUnknownRomTypes(romName);
            assertThat(unknownRomTypes).isEmpty();
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Woody Pop (V1_1).gg|V1_1",
                "Sports Trivia (Prototype-Mar 09, 1995).gg|Prototype-Mar 09, 1995",
                "Olympic Gold - Barcelona '92 (M9).gg|M9",
                "Asterix and the Secret Mission (M2).gg|M2",
                "Xaropinho (Mappy-Hack).gg|Mappy-Hack",
                "Zool (with Invinciblity).gg|with Invinciblity",
        })
        void shouldHaveUnknownRomTypes_WhenRomName_Contains_UnknownRomType(String romName, String expectedUnknownRomType) {
            List<String> unknownRomTypes = romNameHandling.getFilenameUnknownRomTypes(romName);
            assertThat(unknownRomTypes).containsExactly(expectedUnknownRomType);
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