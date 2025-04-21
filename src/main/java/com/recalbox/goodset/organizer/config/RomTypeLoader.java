package com.recalbox.goodset.organizer.config;

import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class RomTypeLoader {

    public static final List<RomTypeMapping> FILENAME_ROM_TYPE_MAPPINGS = loadFilenameRomTypeMappings();
    public static final List<RomTypeMapping> GAMELIST_ROM_TYPE_MAPPINGS = loadGamelistRomTypeMappings();
    public static final List<String> FILENAME_NOT_REPLACED_ROM_TYPES = loadFilenameNotReplacedRomTypes();
    public static final List<String> FILENAME_NOT_REPLACED_ROM_TYPES_REGEX = loadFilenameNotReplacedRomTypesRegex();
    public static final RomTranslationTypeMappings GAMELIST_ROM_TRANSLATION_TYPE_MAPPINGS = loadGamelistRomTranslationTypeMappings();

    private static final String CSV_SEPARATOR = ";";

    private static List<RomTypeMapping> loadFilenameRomTypeMappings() {
        Stream<String> lines = streamResourceLines("/filename-rom-type/rom-type-mappings.csv");
        return toRomTypeMappings(lines);
    }

    private static List<RomTypeMapping> loadGamelistRomTypeMappings() {
        Stream<String> lines = streamResourceLines("/gamelist-rom-type/rom-type-mappings.csv");
        return toRomTypeMappings(lines);
    }

    private static List<RomTypeMapping> toRomTypeMappings(Stream<String> lines) {
        return lines.map(line -> line.split(CSV_SEPARATOR))
                .map(lineTokens -> new RomTypeMapping(lineTokens[0], lineTokens[1]))
                .collect(Collectors.toList());
    }

    private static List<String> loadFilenameNotReplacedRomTypes() {
        return allResourcesLines("/filename-rom-type/not-replaced-rom-types.txt");
    }

    private static List<String> loadFilenameNotReplacedRomTypesRegex() {
        return allResourcesLines("/filename-rom-type/not-replaced-rom-types-regex.txt");
    }

    private static RomTranslationTypeMappings loadGamelistRomTranslationTypeMappings() {
        List<String> lines = allResourcesLines("/gamelist-rom-type/rom-translation-type-mappings.csv");

        String translationTypeRegex = lines.get(0);
        String[] replacementPatterns = lines.get(1).split(CSV_SEPARATOR);
        Map<String, String> matcherGroupTranslations = lines.stream()
                .skip(2)
                .map(line -> line.split(CSV_SEPARATOR))
                .collect(Collectors.toMap(lineTokens -> lineTokens[0], lineTokens -> lineTokens[1]));

        return new RomTranslationTypeMappings(translationTypeRegex, Arrays.asList(replacementPatterns), matcherGroupTranslations);
    }

    private static Stream<String> streamResourceLines(String resourcePath) {
        InputStream romTypeMappingsInputStream = RomTypeLoader.class.getResourceAsStream(resourcePath);
        return FileUtils.readLines(romTypeMappingsInputStream)
                .filter(line -> !line.isEmpty());
    }

    private static List<String> allResourcesLines(String resourcePath) {
        return streamResourceLines(resourcePath)
                .collect(Collectors.toList());
    }

}
