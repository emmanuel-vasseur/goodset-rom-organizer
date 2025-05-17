package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.ConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@RequiredArgsConstructor
@Log
public class GameListTransformer {
    private static final String XML_NAME_START_TAG = "<name>";
    private static final String XML_NAME_TAG_REGEX = "(</?name>)";
    private static final String XML_IMAGE_TAG_REGEX = "(</?image>)";
    private static final String IMAGE_XML_LINE_SUFFIX = ".png</image>";
    private static final String LOWER_QUALITY_GAME_IMAGE_XML_LINE_SUFFIX_TEMPLATE = "/${lower-quality-rom.directory}" + IMAGE_XML_LINE_SUFFIX;
    private static final String FOLDER_IMAGE_XML_LINE_PREFIX_TEMPLATE = "<image>./media/${folder-images.directory}/";
    private static final String GAME_IMAGE_XML_LINE_PREFIX_TEMPLATE = "<image>./media/${game-images.directory}/";
    private static final String IMAGE_TO_FOLDER_PATTERN_TEMPLATE = "^(\\s*<image>./media)/${game-images.directory}/([^/]+)/.*$";
    private static final String IMAGE_TO_FOLDER_SUBSTITUTION_TEMPLATE = "$1/${folder-images.directory}/$2";

    private final List<String> gameListContent;
    private final RomNameHandling romNameHandling;

    private final String folderImageXmlLinePrefix;
    private final String gameImageXmlLinePrefix;
    private final String lowerQualityGameImageXmlLineSuffix;
    private final String imageToFolderRegex;
    private final String imageToFolderSubstitution;

    public GameListTransformer(List<String> gameListContent, RomNameHandling romNameHandling, ConfigProperties config) {
        this.gameListContent = new ArrayList<>(gameListContent);
        this.romNameHandling = romNameHandling;

        folderImageXmlLinePrefix = config.replaceProperties(FOLDER_IMAGE_XML_LINE_PREFIX_TEMPLATE);
        gameImageXmlLinePrefix = config.replaceProperties(GAME_IMAGE_XML_LINE_PREFIX_TEMPLATE);
        lowerQualityGameImageXmlLineSuffix = config.replaceProperties(LOWER_QUALITY_GAME_IMAGE_XML_LINE_SUFFIX_TEMPLATE);
        imageToFolderRegex = config.replaceProperties(IMAGE_TO_FOLDER_PATTERN_TEMPLATE);
        imageToFolderSubstitution = config.replaceProperties(IMAGE_TO_FOLDER_SUBSTITUTION_TEMPLATE);
    }

    public List<String> getGameListContent() {
        return new ArrayList<>(gameListContent);
    }

    public List<String> getFolderImagesThatWillBeReplaced() {
        Map<String, List<String>> imageLinesPerFolder = groupImageLinesPerFolder();
        return imageLinesPerFolder.keySet().stream()
                .filter(gameListContent::contains)
                .map(this::extractFileFromXmlImageTag)
                .collect(Collectors.toList());
    }

    private String extractFileFromXmlImageTag(String folderImageLine) {
        return folderImageLine
                .trim()
                .replaceAll(XML_IMAGE_TAG_REGEX, "")
                .replace("&amp;", "&");
    }

    public void changeFolderImagesByRomImages() {
        Map<String, List<String>> imageLinesPerFolder = groupImageLinesPerFolder();
        gameListContent.replaceAll(line -> replaceFolderImage(line, imageLinesPerFolder));
    }

    private String replaceFolderImage(String line, Map<String, List<String>> imageLinesPerFolder) {
        if (!line.trim().startsWith(folderImageXmlLinePrefix)) {
            return line;
        }

        if (!imageLinesPerFolder.containsKey(line)) {
            log.warning(String.format("No image found for folder '%s'", line));
            return line;
        }

        return imageLinesPerFolder.getOrDefault(line, singletonList(line)).get(0);
    }

    private Map<String, List<String>> groupImageLinesPerFolder() {
        Pattern imageToFolderPattern = Pattern.compile(imageToFolderRegex);
        Map<String, List<String>> imageLinesPrefixPerFolder = gameListContent.stream()
                .filter(line -> line.trim().startsWith(gameImageXmlLinePrefix))
                .collect(Collectors.groupingBy(
                        line -> imageToFolderPattern.matcher(line).replaceFirst(imageToFolderSubstitution)
                ));

        Map<String, List<String>> imageLinesPerFolder = new java.util.HashMap<>();
        imageLinesPrefixPerFolder.forEach((folder, lines) -> {
            imageLinesPerFolder.put(folder + IMAGE_XML_LINE_SUFFIX, lines);
            imageLinesPerFolder.put(folder + lowerQualityGameImageXmlLineSuffix, lines);
        });
        return imageLinesPerFolder;
    }

    public void renameRomVariations() {
        gameListContent.replaceAll(this::replaceRomVariationsInRomName);
    }

    private String replaceRomVariationsInRomName(String line) {
        if (isXmlNameNode(line)) {
            return romNameHandling.replaceGameListRomVariations(line);
        }
        return line;
    }

    private boolean isXmlNameNode(String line) {
        return line.trim().startsWith(XML_NAME_START_TAG);
    }

    public Map<String, List<String>> getGameListRomNamesWithUnknownRomVariations() {
        return romNameHandling.getRomNamesWithUnknownRomVariations(getAllRomNames(), romNameHandling::getGameListUnknownRomVariations);
    }

    private Stream<String> getAllRomNames() {
        return gameListContent.stream()
                .filter(this::isXmlNameNode)
                .map(line -> line.replaceAll(XML_NAME_TAG_REGEX, "").trim());
    }
}
