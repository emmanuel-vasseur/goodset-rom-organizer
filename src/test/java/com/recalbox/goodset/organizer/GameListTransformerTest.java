package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.ConfigProperties;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.recalbox.goodset.organizer.TestUtils.loadGameListLines;
import static org.assertj.core.api.Assertions.assertThat;

class GameListTransformerTest {

    @Nested
    class ChangeFolderImagesTests {

        @Test
        void shouldChangeFolderImages_WhenOnlyOneRomExistsPerGame() {
            GameListTransformer gameListTransformer = createGameListTransformer("/multiple-games-with-one-rom.gamelist.xml");

            gameListTransformer.changeFolderImagesByRomImages();
            List<String> transformedContent = gameListTransformer.getGameListContent();

            assertThat(keepOnlyGameImageAndFolderTags(transformedContent)).containsExactly(
                    "<image>./media/images/5 in 1 Funpak/5 in 1 Funpak (USA) [!].png</image>",
                    "</game>",
                    "<image>./media/images/Addams Family, The/Addams Family, The (World) [!].png</image>",
                    "</game>",
                    "<image>./media/images/5 in 1 Funpak/5 in 1 Funpak (USA) [!].png</image>",
                    "</folder>",
                    "<image>./media/images/Addams Family, The/Addams Family, The (World) [!].png</image>",
                    "</folder>"
            );
        }

        @Test
        void shouldChangeFolderImagesByFirstRomImage_When_NoOrMultipleRomExistsPerGame() {
            GameListTransformer gameListTransformer = createGameListTransformer("/multiple-games-with-unknown-one-or-multiple-roms.gamelist.xml");

            gameListTransformer.changeFolderImagesByRomImages();
            List<String> transformedContent = gameListTransformer.getGameListContent();

            assertThat(keepOnlyGameImageAndFolderTags(transformedContent)).containsExactly(
                    "<image>./media/images/5 in 1 Funpak/5 in 1 Funpak (USA) [!].png</image>",
                    "</game>",
                    // Missing "Alex Kidd in Miracle World" image
                    "</game>",
                    "<image>./media/images/Alien 3/Alien 3 (Europe, USA) [!].png</image>",
                    "</game>",
                    "<image>./media/images/Alien 3/Alien 3 (Japan) [!].png</image>",
                    "</game>",
                    "<image>./media/images/Arcade Classics/Arcade Classics (USA) [!].png</image>",
                    "</game>",

                    "<image>./media/images/5 in 1 Funpak/5 in 1 Funpak (USA) [!].png</image>",
                    "</folder>",
                    "<image>./media/folders/Alex Kidd in Miracle World.png</image>",
                    "</folder>",
                    "<image>./media/images/Alien 3/Alien 3 (Europe, USA) [!].png</image>",
                    "</folder>",
                    "<image>./media/images/Arcade Classics/Arcade Classics (USA) [!].png</image>",
                    "</folder>"
            );
        }

        private List<String> keepOnlyGameImageAndFolderTags(List<String> transformedContent) {
            Pattern keepTagPattern = Pattern.compile("</(image|game|folder)>");
            return transformedContent.stream()
                    .filter(line -> keepTagPattern.matcher(line).find())
                    .map(line -> line.replaceAll("^\\s*", ""))
                    .collect(Collectors.toList());
        }
    }

    @Nested
    class GetFolderImagesTests {
        @Test
        void shouldGetFolderImagesThatWillBeReplaced_OnlyIfAssociatedRomsHasImages() {
            GameListTransformer gameListTransformer = createGameListTransformer("/multiple-games-with-unknown-one-or-multiple-roms.gamelist.xml");

            List<String> folderImagesThatWillBeReplaced = gameListTransformer.getFolderImagesThatWillBeReplaced();

            assertThat(folderImagesThatWillBeReplaced).containsExactlyInAnyOrder(
                    "./media/folders/5 in 1 Funpak.png",
                    // Missing "Alex Kidd in Miracle World" image
                    "./media/folders/Alien 3.png",
                    "./media/folders/Arcade Classics.png"
            );
        }

        @Test
        void shouldGetOtherVersionFolderImagesThatWillBeReplaced() {
            GameListTransformer gameListTransformer = createGameListTransformer("/multiple-games-with-multiple-roms.gamelist.xml");

            List<String> folderImagesThatWillBeReplaced = gameListTransformer.getFolderImagesThatWillBeReplaced();

            assertThat(folderImagesThatWillBeReplaced).containsExactlyInAnyOrder(
                    "./media/folders/Aerial Assault.png",
                    "./media/folders/Aerial Assault/Other versions.png",
                    "./media/folders/Aladdin.png",
                    "./media/folders/Aladdin/Other versions.png"
            );
        }

        @Test
        void shouldGetFolderImagesThatWillBeReplaced_ExcapeXmlCharacters() {
            GameListTransformer gameListTransformer = new GameListTransformer(Arrays.asList(
                    "<image>./media/images/Sonic &amp; Tails 2/Sonic &amp; Tails 2 (J) [!].gg</image>",
                    "<image>./media/folders/Sonic &amp; Tails 2.png</image>"
            ), new RomNameHandling(), new ConfigProperties());

            List<String> folderImagesThatWillBeReplaced = gameListTransformer.getFolderImagesThatWillBeReplaced();

            assertThat(folderImagesThatWillBeReplaced).containsExactlyInAnyOrder(
                    "./media/folders/Sonic & Tails 2.png"
            );
        }
    }

    private GameListTransformer createGameListTransformer(String name) {
        List<String> gameListLines = loadGameListLines(name);
        return new GameListTransformer(gameListLines, new RomNameHandling(), new ConfigProperties());
    }

}