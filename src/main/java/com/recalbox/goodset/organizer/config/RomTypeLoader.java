package com.recalbox.goodset.organizer.config;

import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class RomTypeLoader {

    public static final List<RomTypeMapping> FILENAME_ROM_TYPE_MAPPINGS = loadFilenameRomTypeMappings();
    public static final List<RomTypeMapping> GAMELIST_ROM_TYPE_MAPPINGS = loadGamelistRomTypeMappings();
    public static final List<RomTypeMapping> FILENAME_ROM_TYPE_REGEX_MAPPINGS = loadFilenameRomTypeRegexMappings();
    public static final List<RomTypeMapping> GAMELIST_ROM_TYPE_REGEX_MAPPINGS = loadGamelistRomTypeRegexMappings();

    private static final String CSV_SEPARATOR = ";";

    private static List<RomTypeMapping> loadFilenameRomTypeMappings() {
        Stream<String> lines = streamResourceLines("/rom-type-mappings.csv");
        return toRomTypeMappings(lines, 0, 1);
    }

    private static List<RomTypeMapping> loadGamelistRomTypeMappings() {
        Stream<String> lines = streamResourceLines("/rom-type-mappings.csv");
        return toRomTypeMappings(lines, 2, 3);
    }

    private static List<RomTypeMapping> loadFilenameRomTypeRegexMappings() {
        Stream<String> lines = streamResourceLines("/rom-type-regex-mappings.csv");
        return toRomTypeMappings(lines, 0, 1);
    }

    private static List<RomTypeMapping> loadGamelistRomTypeRegexMappings() {
        Stream<String> lines = streamResourceLines("/rom-type-regex-mappings.csv");
        return toRomTypeMappings(lines, 0, 2);
    }

    private static List<RomTypeMapping> toRomTypeMappings(Stream<String> lines, int sourceIndex, int replacementIndex) {
        return lines.map(line -> line.split(CSV_SEPARATOR))
                .map(lineTokens -> new RomTypeMapping(lineTokens[sourceIndex], lineTokens[replacementIndex]))
                .filter(mapping -> !mapping.getRomTypeToken().isEmpty())
                .collect(Collectors.toList());
    }

    private static Stream<String> streamResourceLines(String resourcePath) {
        InputStream romTypeMappingsInputStream = RomTypeLoader.class.getResourceAsStream(resourcePath);
        return FileUtils.readLines(romTypeMappingsInputStream)
                .filter(line -> !line.isEmpty());
    }

}
