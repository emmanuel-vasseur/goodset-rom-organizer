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
    class ReplaceRomVariationsTests {

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Crystal Warriors (UE).gg|Crystal Warriors (EU)(US).gg",
                "James Bond 007 - The Duel (E) [!].gg|James Bond 007 - The Duel (EU) [!].gg",
                "Jang Pung II (K).gg|Jang Pung II (KR).gg",
                "Woody Pop (UEB) (V1.1) [!].gg|Woody Pop (EU)(US) (V1.1) [!].gg",
                "Ultimate Soccer (JEB) [!].gg|Ultimate Soccer (EU)(JP) [!].gg",
                "Tails' Adventures (JU) [!].gg|Tails' Adventures (US)(JP) [!].gg",
                "Surf Ninjas (UB) [!].gg|Surf Ninjas (US) [!].gg",
                "Super Monaco GP (JK).gg|Super Monaco GP (JP)(KR).gg",
                "Crystal Warriors (UE).gg|Crystal Warriors (EU)(US).gg",
                "Sonic The Hedgehog (JE) (V1.1) [!].gg|Sonic The Hedgehog (EU)(JP) (V1.1) [!].gg",
                "Incredible Crash Dummies, The (JUE) [!].gg|Incredible Crash Dummies, The (World) [!].gg",
                "Iron Man X-O Manowar in Heavy Metal (U).gg|Iron Man X-O Manowar in Heavy Metal (US).gg",
                "Alex Kidd in Miracle World (T) [S].sms|Alex Kidd in Miracle World (TW) [S].sms",

                "Ax Battler - A Legend of Golden Axe [T+Fre20060926_Rysley].gg|Ax Battler - A Legend of Golden Axe (FR)[T+Fre20060926_Rysley].gg",
                "Crystal Warriors [T-Fre].gg|Crystal Warriors (FR)[T-Fre].gg",
                "Crystal Warriors [T+Fre.99_Asmodeath].gg|Crystal Warriors (FR)[T+Fre.99_Asmodeath].gg",
                "Shining Force Gaiden - Final Conflict [T-Eng].gg|Shining Force Gaiden - Final Conflict (UK)[T-Eng].gg",
                "Phantasy Star Gaiden [T+Bra_CBT].gg|Phantasy Star Gaiden (PT)[T+Bra_CBT].gg",
                "Megaman [T+Ger1.00_Star-trans].gg|Megaman (DE)[T+Ger1.00_Star-trans].gg",
                "Ax Battler - A Legend of Golden Axe [T+Spa100_pkt].gg|Ax Battler - A Legend of Golden Axe (ES)[T+Spa100_pkt].gg",
                "Shinobi II - The Silent Fury [T+Rusbeta3_Lupus].gg|Shinobi II - The Silent Fury (RU)[T+Rusbeta3_Lupus].gg",
                "Bishoujo Senshi Sailor Moon - Another Story (J) [T+Eng1.00_BishoujoST,RHFix1.0_mteam].sfc|Bishoujo Senshi Sailor Moon - Another Story (JP) (UK)[T+Eng1.00_BishoujoST,RHFix1.0_mteam].sfc",

                "Dai-3-Ji Super Robot Taisen (J) (V1.1) [T+En1.00_Aeon Genesis].sfc|Dai-3-Ji Super Robot Taisen (JP) (V1.1) (UK)[T+Eng1.00_Aeon Genesis].sfc",
                "BS Legend of Zelda, The - Ancient Stone Tablets - Master Quest - Chapter 1 (J) (BS) [T-En by Euclid v1.0] [n].sfc|BS Legend of Zelda, The - Ancient Stone Tablets - Master Quest - Chapter 1 (JP)(BS) (UK)[T-Eng by Euclid v1.0] [n].sfc",
                "Marvel Super Heroes - War of the Gems (Eng).sfc|Marvel Super Heroes - War of the Gems (US).sfc",
                "RoboCop 3 (U) (eng).sfc|RoboCop 3 (US).sfc",
                "Legend of Zelda, The - Parallel Remodel Worlds (Hack Sprites) (U).sfc|Legend of Zelda, The - Parallel Remodel Worlds (Hack Sprites) (US).sfc",
                "Super Fire Pro Wrestling 2 (J) (Final Prototype).sfc|Super Fire Pro Wrestling 2 (JP) (Final Prototype).sfc",
                "RPM Racing (Prototype-08-14-91).sfc|RPM Racing (Prototype 08-14-91).sfc",

                "3ji no Wide Shou (BS).sfc|3ji no Wide Shou (JP)(BS).sfc",
                "Arkanoid - Doh It Again (J) (BS).sfc|Arkanoid - Doh It Again (JP)(BS).sfc",
                "ASCII Music Tool (1995.11.02 v1.0) (BS).sfc|ASCII Music Tool (1995.11.02 v1.0) (JP)(BS).sfc",
                "SD Ultra Battle - Seven Densetsu + ST BIOS (ST) [h1].sfc|SD Ultra Battle - Seven Densetsu (JP)(ST) [h1].sfc",
                "Poi Poi Ninja World (ST) [!].sfc|Poi Poi Ninja World (JP)(ST) [!].sfc",
        })
        void replaceEachFilenameRomVariation(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceFilenameRomVariations(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Crystal Warriors [EU,US]|Crystal Warriors [Europe, USA]",
                "James Bond 007 - The Duel [EU] (Best)|James Bond 007 - The Duel (Best Version) [Europe]",
                "Jang Pung II [KR]|Jang Pung II [Korea]",
                "XPMCK v15 - Scotland by mic_ (PD)|XPMCK v15 - Scotland by mic_ (Public Domain)",
                "Woody Pop [EU,US] (Best)|Woody Pop (Best Version) [Europe, USA]",
                "Tails' Adventures [US,JP] (Best)|Tails' Adventures (Best Version) [USA, Japan]",
                "Surf Ninjas [US] (Best)|Surf Ninjas (Best Version) [USA]",
                "Super Monaco GP [JP,KR]|Super Monaco GP [Japan, Korea]",
                "Crystal Warriors [EU,US]|Crystal Warriors [Europe, USA]",
                "Sonic The Hedgehog [EU,JP] (Best)|Sonic The Hedgehog (Best Version) [Europe, Japan]",
                "Iron Man X-O Manowar in Heavy Metal [US]|Iron Man X-O Manowar in Heavy Metal [USA]",

                "Iron Man X-O Manowar in Heavy Metal [US] (Proto.)|Iron Man X-O Manowar in Heavy Metal [USA] (Prototype)",
                "Iron Man X-O Manowar in Heavy Metal [US] (Alt.)|Iron Man X-O Manowar in Heavy Metal [USA] (Alternate)",
                "Indiana Jones and the Last Crusade [EU] (M5)|Indiana Jones and the Last Crusade [Europe] (5 Languages)",
                "ZZZ(notgame):Kinetic Connection|[Not a game] Kinetic Connection",
                "Zool [JP] [hI]|Zool [Japan] [Intro Hack]",
                "Tick, The (EU) [p1+C]|Tick, The (Europe) [Pirate (1+C)]",
                "Carrier Aces (US) [hIR]|Carrier Aces (USA) [Intro Hack (R)]",
                "Alex Kidd in Miracle World [TW] [S]|Alex Kidd in Miracle World [Taiwan] [MasterSystem Mode]",
                "Monopoly (japan) (JP) [!] [JP]|Monopoly [Best Version] (Japan)"
        })
        void replaceEachGameListRomVariation(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceGameListRomVariations(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Ax Battler - A Legend of Golden Axe (FR)[T+Fre20060926_Rysley]|Ax Battler - A Legend of Golden Axe [French Translation (20060926_Rysley)]",
                "Crystal Warriors (FR)[T-Fre]|Crystal Warriors [French Older Translation]",
                "Crystal Warriors (FR) [T+Fre.99_Asmodeath]|Crystal Warriors [French Translation (.99_Asmodeath)]",
                "Shining Force Gaiden - Final Conflict (UK)[T-Eng]|Shining Force Gaiden - Final Conflict [English Older Translation]",
                "Phantasy Star Gaiden (PT) [T+Bra_CBT]|Phantasy Star Gaiden [Portuguese Translation (CBT)]",
                "Megaman (DE)[T+Ger1.00_Star-trans]|Megaman [German Translation (1.00_Star-trans)]",
                "Ax Battler - A Legend of Golden Axe (ES)[T+Spa100_pkt]|Ax Battler - A Legend of Golden Axe [Spanish Translation (100_pkt)]",
                "Shinobi II - The Silent Fury (RU) [T+Rusbeta3_Lupus]|Shinobi II - The Silent Fury [Russian Translation (beta3_Lupus)]",
                "Shinobi II - The Silent Fury (RU)[T+Rusbeta3_Lupus] (NG-Dump Known)|Shinobi II - The Silent Fury [Russian Translation (beta3_Lupus)] (Redump Needed)",
        })
        void replaceAllRomTranslationVariation(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceGameListRomVariations(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

        @Test
        void replaceMultipleDifferentRomVariations() {
            String newFilenameRomName = romNameHandling.replaceFilenameRomVariations("Ax Battler - A Legend of Golden Axe (UE) (V2.4) [T+Fre][a1].gg");
            assertThat(newFilenameRomName).isEqualTo("Ax Battler - A Legend of Golden Axe (EU)(US) (V2.4) (FR)[T+Fre][a1].gg");

            String newGameListRomName = romNameHandling.replaceGameListRomVariations("Ax Battler - A Legend of Golden Axe (EU) (US) (V2.4) (FR) [T+Fre][a1].gg");
            assertThat(newGameListRomName).isEqualTo("Ax Battler - A Legend of Golden Axe (Europe, USA) (V2.4) [French Translation][Alternate (1)].gg");
        }

        @Test
        void shouldNotReplaceUnknownRomVariations() {
            String newRomName = romNameHandling.replaceFilenameRomVariations("FIFA International Soccer (Blabla) [genial].gg");
            assertThat(newRomName).isEqualTo("FIFA International Soccer (Blabla) [genial].gg");
        }

        @Test
        void replaceMultipleTimesSameRomVariation() {
            String newRomName = romNameHandling.replaceGameListRomVariations("Jang Pung II [o1] [o2] (FR) [T-Fre] (FR) [T+Fre]");
            assertThat(newRomName).isEqualTo("Jang Pung II [OverDump (1)] [OverDump (2)] [French Older Translation] [French Translation]");
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "Ax Battler - A Legend of Golden Axe (FR) [T+Fre20060926_Rysley] (Trans.)|Ax Battler - A Legend of Golden Axe [French Translation (20060926_Rysley)]",
                "Crystal Warriors [!] (Best)|Crystal Warriors [Best Version]",
                "Crystal Warriors (FR) [T-Fre] (Trans.)|Crystal Warriors [French Older Translation]",
                "Shining Force Gaiden - Final Conflict (Prototype 42) (Prototype)|Shining Force Gaiden - Final Conflict (Prototype 42)",
                "Phantasy Star Gaiden (World) [WOR]|Phantasy Star Gaiden (World)",
                "Megaman [a3] (Alt.)|Megaman [Alternate (3)]",
                "Ax Battler - A Legend of Golden Axe (EU) (JP) [EU,JP]|Ax Battler - A Legend of Golden Axe (Europe, Japan)",
                "Ax Battler - A Legend of Golden Axe (EU) (JP) [EU,JP]|Ax Battler - A Legend of Golden Axe (Europe, Japan)",
                "Mappy (Mappy Hack) (Hack)|Mappy (Mappy Hack)",
                "Flubber (Unl) [!] (Unl.)|Flubber (Unlicensed) [Best Version]",
                "Chrono Trigger - Crimson Echoes (98% Complete Beta) (Hack) (Beta)|Chrono Trigger - Crimson Echoes (98% Complete Beta) (Hack)",
                "Ultima - Runes of Virtue II (Beta1994) [b2] (Beta)|Ultima - Runes of Virtue II (Beta 1994) [Bad Dump (2)]",
                "Mr. Tuff (Beta-Jul1994) (M3) (Beta)|Mr. Tuff (Beta Jul1994) (3 Languages)",
                "Super Mario World - The Final Journey (Demo1) (SMW1 Hack) (Hack) (Demo)|Super Mario World - The Final Journey (Demo 1) (SMW1 Hack)",
                "Bowser's Return (Demo 4) (Beta 1) by MasterRPGr (TheRPGLPer) (2010-03-05) (SMW1 Hack) (Beta) (Hack) (Demo)|Bowser's Return (Demo 4) (Beta 1) by MasterRPGr (TheRPGLPer) (2010-03-05) (SMW1 Hack)",

                "Mickey Mouse no Castle Illusion (JP) [S] [h1C] [JP](Hack)|Mickey Mouse no Castle Illusion (Japan) [MasterSystem Mode] [Hack (1C)]",
                "Tom and Jerry : The Movie (EU) (US) [t1] [hIR] [EU,US](Hack)|Tom and Jerry : The Movie (Europe, USA) [Training (1)] [Intro Hack (R)]",
                "Shinobi II - The Silent Fury (EU) (US) [!] [a2C] [S] [EU,US] (Alt.) (Best)|Shinobi II - The Silent Fury [Best Version] (Europe, USA) [Alternate (2C)] [MasterSystem Mode]",
                "Shinobi II - The Silent Fury (World) [!] [a2+C] [S] [EU,US] (Alt.) (Best)|Shinobi II - The Silent Fury [Best Version] (World) [Alternate (2+C)] [MasterSystem Mode] [Europe, USA]",
        })
        void replaceRemoveDuplicatedRomVariations(String romName, String expectedRomName) {
            String newRomName = romNameHandling.replaceGameListRomVariations(romName);
            assertThat(newRomName).isEqualTo(expectedRomName);
        }

    }

    @Nested
    class UnknownRomVariationsTests {

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
        void shouldFilenameNotHaveUnknownRomVariations_WhenRomName_Contains_KnownRomVariationMapping(String romName) {
            List<String> unknownRomVariations = romNameHandling.getFilenameUnknownRomVariations(romName);
            assertThat(unknownRomVariations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Crystal Warriors (EU) (US) [EU,US]",
                "James Bond 007 - The Duel (EU) [EU]",
                "Jang Pung II (KR) [KR]",
                "XPMCK v15 - Scotland by mic_ (PD)",
                "Ultimate Soccer (World) [WOR]",
                "Tails' Adventures (US) (JP) [US,JP]",
                "Super Monaco GP (JP) (KR) [JP,KR]",
                "Sonic The Hedgehog (EU) (JP) [EU,JP]",
                "Iron Man X-O Manowar in Heavy Metal (US) [US]",
                "Iron Man X-O Manowar in Heavy Metal (US) [b1] [US]",
                "Iron Man X-O Manowar in Heavy Metal (US) [a1] [US]",
                "Indiana Jones and the Last Crusade (EU) (US) [t1] [EU,US]",
                "Kinetic Connection (JP) [o1] [JP]",
                "Zool (JP) [hI] [JP]",
                "Jang Pung II [S]",
        })
        void shouldGameListNotHaveUnknownRomVariations_WhenRomName_Contains_KnownRomVariationMapping(String romName) {
            List<String> unknownRomVariations = romNameHandling.getGameListUnknownRomVariations(romName);
            assertThat(unknownRomVariations).isEmpty();
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
        void shouldFilenameNotHaveUnknownRomVariations_WhenRomName_Contains_KnownRomTranslationVariationMapping(String romName) {
            List<String> unknownRomVariations = romNameHandling.getFilenameUnknownRomVariations(romName);
            assertThat(unknownRomVariations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Crystal Warriors (FR)[T-Fre]",
                "Crystal Warriors (FR) [T+Fre.99_Asmodeath]",
                "Phantasy Star Gaiden (PT)[T+Bra_CBT]",
                "Megaman (DE) [T+Ger1.00_Star-trans]",
                "Ax Battler - A Legend of Golden Axe (ES) [T+Spa100_pkt]",
                "Shinobi II - The Silent Fury (RU)[T+Rusbeta3_Lupus]",
                "Shinobi II - The Silent Fury (RU)[T+Rusbeta3_Lupus] (NG-Dump Known)",
        })
        void shouldGameListNotHaveUnknownRomVariations_WhenRomName_Contains_KnownRomTranslationVariationMapping(String romName) {
            List<String> unknownRomVariations = romNameHandling.getGameListUnknownRomVariations(romName);
            assertThat(unknownRomVariations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "James Bond 007 - The Duel [!].gg",
                "Taz in Escape from Mars - Star Wars Text (Hack).gg",
                "Double Dragon (Prototype).gg",
                "Sonic & Tails (Demo).gg",
                "Sonic Drift (Sample).gg",
        })
        void shouldNotHaveUnknownRomVariations_WhenRomName_Contains_KnownNotReplacedRomVariations(String romName) {
            List<String> unknownFilenameRomVariations = romNameHandling.getFilenameUnknownRomVariations(romName);
            assertThat(unknownFilenameRomVariations).isEmpty();

            List<String> unknownGameListRomVariations = romNameHandling.getGameListUnknownRomVariations(romName);
            assertThat(unknownGameListRomVariations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Woody Pop (V1.1).gg",
                "Sports Trivia (Prototype - Mar 09, 1995).gg",
                "Olympic Gold - Barcelona '92 (M8).gg",
                "Asterix and the Secret Mission (M3).gg",
                "Xaropinho (Mappy Hack).gg",
        })
        void shouldNotHaveUnknownRomVariations_WhenRomName_Contains_KnownNotRegexReplacedRomVariations(String romName) {
            List<String> unknownGameListRomVariations = romNameHandling.getGameListUnknownRomVariations(romName);
            assertThat(unknownGameListRomVariations).isEmpty();

            List<String> unknownFilenameRomVariations = romNameHandling.getFilenameUnknownRomVariations(romName);
            assertThat(unknownFilenameRomVariations).isEmpty();
        }

        @ParameterizedTest
        @CsvSource(delimiter = '|', value = {
                "ASMT2 by the Talkhaus Community (V1.55+).sfc|(V1.55+)",
                "Sports Trivia (Proto Mar 09, 1995).gg|(Proto Mar 09, 1995)",
                "Olympic Gold - Barcelona '92 (M9).gg|(M9)",
                "Asterix and the Secret Mission (M1).gg|(M1)",
                "Xaropinho (Mappy-Hack).gg|(Mappy-Hack)",
                "Zool (with Invinciblity).gg|(with Invinciblity)",
        })
        void shouldHaveUnknownRomVariations_WhenRomName_Contains_UnknownRomVariation(String romName, String expectedUnknownRomVariation) {
            List<String> unknownRomVariations = romNameHandling.getFilenameUnknownRomVariations(romName);
            assertThat(unknownRomVariations).containsExactly(expectedUnknownRomVariation);
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