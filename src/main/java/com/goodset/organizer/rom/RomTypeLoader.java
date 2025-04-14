package com.goodset.organizer.rom;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
public class RomTypeLoader {

    public static final List<RomTypeFileMapping> ROM_TYPE_FILE_MAPPINGS = loadRomTypeFileMappings();
    public static final List<String> NOT_REPLACED_ROM_TYPES = loadNotReplacedRomTypes();
    public static final List<String> NOT_REPLACED_ROM_TYPES_REGEX = loadNotReplacedRomTypesRegex();

    private static List<RomTypeFileMapping> loadRomTypeFileMappings() {
        InputStream romTypeMappingsInputStream = RomTypeLoader.class.getResourceAsStream("/rom-type-mappings.csv");
        return new BufferedReader(new InputStreamReader(romTypeMappingsInputStream, UTF_8))
                .lines()
                .filter(line -> !line.isEmpty())
                .map(line -> line.split(";"))
                .map(lineTokens -> new RomTypeFileMapping(lineTokens[0], lineTokens[1]))
                .collect(Collectors.toList());
    }

    private static List<String> loadNotReplacedRomTypes() {
        InputStream romTypeMappingsInputStream = RomTypeLoader.class.getResourceAsStream("/not-replaced-rom-types.txt");
        return new BufferedReader(new InputStreamReader(romTypeMappingsInputStream, UTF_8))
                .lines()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    private static List<String> loadNotReplacedRomTypesRegex() {
        InputStream romTypeMappingsInputStream = RomTypeLoader.class.getResourceAsStream("/not-replaced-rom-types-regex.txt");
        return new BufferedReader(new InputStreamReader(romTypeMappingsInputStream, UTF_8))
                .lines()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

}
