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

    public static final List<RomTypeMapping> ROM_TYPE_MAPPINGS = loadRomTypeFileMappings();
    public static final List<String> NOT_REPLACED_ROM_TYPES = loadNotReplacedRomTypes();
    public static final List<String> NOT_REPLACED_ROM_TYPES_REGEX = loadNotReplacedRomTypesRegex();
    public static final RomTranslationTypeMappings ROM_TRANSLATION_TYPE_MAPPINGS = loadRomTranslationTypeMappings();

    private static final String CSV_SEPARATOR = ";";

    private static List<RomTypeMapping> loadRomTypeFileMappings() {
        Stream<String> lines = getResourceLines("/rom-type-mappings.csv");
        return lines.map(line -> line.split(CSV_SEPARATOR))
                .map(lineTokens -> new RomTypeMapping(lineTokens[0], lineTokens[1]))
                .collect(Collectors.toList());
    }

    private static List<String> loadNotReplacedRomTypes() {
        Stream<String> lines = getResourceLines("/not-replaced-rom-types.txt");
        return lines.collect(Collectors.toList());
    }

    private static List<String> loadNotReplacedRomTypesRegex() {
        Stream<String> lines = getResourceLines("/not-replaced-rom-types-regex.txt");
        return lines.collect(Collectors.toList());
    }

    private static RomTranslationTypeMappings loadRomTranslationTypeMappings() {
        List<String> lines = getResourceLines("/rom-translation-type-mappings.csv")
                .collect(Collectors.toList());

        String translationTypeRegex = lines.get(0);
        String[] replacementPatterns = lines.get(1).split(CSV_SEPARATOR);
        Map<String, String> matcherGroupTranslations = lines.stream()
                .skip(2)
                .map(line -> line.split(CSV_SEPARATOR))
                .collect(Collectors.toMap(lineTokens -> lineTokens[0], lineTokens -> lineTokens[1]));

        return new RomTranslationTypeMappings(translationTypeRegex, Arrays.asList(replacementPatterns), matcherGroupTranslations);
    }

    private static Stream<String> getResourceLines(String resourcePath) {
        InputStream romTypeMappingsInputStream = RomTypeLoader.class.getResourceAsStream(resourcePath);
        return FileUtils.readLines(romTypeMappingsInputStream);
    }

}
