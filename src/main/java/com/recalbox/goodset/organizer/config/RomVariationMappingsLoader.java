package com.recalbox.goodset.organizer.config;

import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class RomVariationMappingsLoader {

    public static final List<RomVariationMapping> FILENAME_ROM_VARIATION_MAPPINGS = loadFilenameRomVariationMappings();
    public static final List<RomVariationMapping> FILENAME_ROM_VARIATION_REGEX_MAPPINGS = loadFilenameRegexRomVariationMappings();
    public static final List<RomVariationMapping> FILENAME_TO_GAMELIST_ROM_VARIATION_MAPPINGS = loadFilenameToGameListRomVariationMappings();
    public static final List<RomVariationMapping> GAMELIST_ROM_VARIATION_MAPPINGS = loadGameListLiteralRomVariationMappings();
    public static final List<RomVariationMapping> GAMELIST_ROM_VARIATION_REGEX_MAPPINGS = loadGameListRegexRomVariationMappings();
    private static final List<RomVariationMapping> REGION_ROM_VARIATION_MAPPINGS = loadRegionRomVariationMappings();

    private static final String CSV_SEPARATOR = ";";
    private static final String COMMENT_PREFIX = "#";

    private static final int FILENAME_LITERAL_SOURCE_INDEX = 0;
    private static final int FILENAME_LITERAL_REPLACEMENT_INDEX = 1;
    private static final int GAMELIST_LITERAL_SOURCE_INDEX = 2;
    private static final int GAMELIST_LITERAL_REPLACEMENT_INDEX = 3;
    private static final int MATCHING_REGIONS_INDEX = 4;

    private static final int REGEX_SOURCE_INDEX = 0;
    private static final int FILENAME_REGEX_REPLACEMENT_INDEX = 1;
    private static final int GAMELIST_REGEX_REPLACEMENT_INDEX = 2;

    public static Set<String> extractRegions(String romName) {
        return RomVariationMapping.extractRegions(romName, REGION_ROM_VARIATION_MAPPINGS);
    }

    private static List<RomVariationMapping> loadFilenameRomVariationMappings() {
        Stream<String> lines = streamLiteralRomVariationMappings();
        return toRomVariationMappings(lines, FILENAME_LITERAL_SOURCE_INDEX, FILENAME_LITERAL_REPLACEMENT_INDEX);
    }

    private static List<RomVariationMapping> loadGameListLiteralRomVariationMappings() {
        Stream<String> lines = streamLiteralRomVariationMappings();
        return toRomVariationMappings(lines, GAMELIST_LITERAL_SOURCE_INDEX, GAMELIST_LITERAL_REPLACEMENT_INDEX);
    }

    private static List<RomVariationMapping> loadFilenameToGameListRomVariationMappings() {
        Stream<String> lines = streamLiteralRomVariationMappings();
        return streamRomVariationMapping(lines, FILENAME_LITERAL_REPLACEMENT_INDEX, GAMELIST_LITERAL_SOURCE_INDEX)
                .filter(RomVariationMapping::isExtraWhiteSpacesMapping)
                .collect(Collectors.toList());
    }

    private static List<RomVariationMapping> loadRegionRomVariationMappings() {
        Stream<String> lines = streamLiteralRomVariationMappings();
        return toRomVariationMappings(lines, GAMELIST_LITERAL_SOURCE_INDEX, MATCHING_REGIONS_INDEX);
    }

    private static List<RomVariationMapping> loadFilenameRegexRomVariationMappings() {
        Stream<String> lines = streamRegexRomVariationMappings();
        return toRomVariationMappings(lines, REGEX_SOURCE_INDEX, FILENAME_REGEX_REPLACEMENT_INDEX);
    }

    private static List<RomVariationMapping> loadGameListRegexRomVariationMappings() {
        Stream<String> lines = streamRegexRomVariationMappings();
        return toRomVariationMappings(lines, REGEX_SOURCE_INDEX, GAMELIST_REGEX_REPLACEMENT_INDEX);
    }

    private static List<RomVariationMapping> toRomVariationMappings(Stream<String> lines, int sourceIndex, int replacementIndex) {
        return streamRomVariationMapping(lines, sourceIndex, replacementIndex)
                .collect(Collectors.toList());
    }

    private static Stream<RomVariationMapping> streamRomVariationMapping(Stream<String> lines, int sourceIndex, int replacementIndex) {
        return lines.map(line -> line.split(CSV_SEPARATOR))
                .filter(lineTokens -> lineTokens.length > Math.max(sourceIndex, replacementIndex))
                .map(lineTokens -> new RomVariationMapping(lineTokens[sourceIndex], lineTokens[replacementIndex]))
                .filter(RomVariationMapping::isValidMapping);
    }

    private static Stream<String> streamLiteralRomVariationMappings() {
        return streamResourceLines("/rom-variation-mappings.csv");
    }

    private static Stream<String> streamRegexRomVariationMappings() {
        return streamResourceLines("/rom-variation-regex-mappings.csv");
    }

    private static Stream<String> streamResourceLines(String resourcePath) {
        InputStream romVariationMappingsInputStream = RomVariationMappingsLoader.class.getResourceAsStream(resourcePath);
        return FileUtils.readLines(romVariationMappingsInputStream)
                .filter(RomVariationMappingsLoader::isValidMappingLine);
    }

    private static boolean isValidMappingLine(String line) {
        boolean isNotBlank = !line.trim().isEmpty();
        boolean isNotComment = !line.startsWith(COMMENT_PREFIX);
        return isNotBlank && isNotComment;
    }
}
