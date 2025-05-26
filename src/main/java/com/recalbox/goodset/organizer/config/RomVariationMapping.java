package com.recalbox.goodset.organizer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public class RomVariationMapping {
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    public static final String REGION_SEPARATOR = ",";

    private final String romVariationSource;
    private final String romVariationReplacement;

    public static String replaceLiterals(String romName, List<RomVariationMapping> literalRomVariationMappings) {
        return applyAll(romName, literalRomVariationMappings, RomVariationMapping::literalReplace);
    }

    public static String replaceRegexMatches(String romName, List<RomVariationMapping> regexRomVariationMappings) {
        return applyAll(romName, regexRomVariationMappings, RomVariationMapping::regexReplace);
    }

    public static String removeLiterals(String romName, List<RomVariationMapping> literalRomVariationMappings) {
        return applyAll(romName, literalRomVariationMappings, RomVariationMapping::literalRemove);
    }

    public static String removeRegexMatches(String romName, List<RomVariationMapping> regexRomVariationMappings) {
        return applyAll(romName, regexRomVariationMappings, RomVariationMapping::regexRemove);
    }

    public static Set<String> extractRegions(String romName, List<RomVariationMapping> regionRomVariationMappings) {
        return regionRomVariationMappings.stream()
                .flatMap(regions -> regions.extractRegions(romName))
                .collect(Collectors.toSet());
    }

    private static String applyAll(String romName, List<RomVariationMapping> romVariationMappings, BiFunction<RomVariationMapping, String, String> operation) {
        String newRomName = romName;
        for (RomVariationMapping mapping : romVariationMappings) {
            newRomName = operation.apply(mapping, newRomName);
        }
        return newRomName;
    }

    public boolean isValidMapping() {
        boolean tokenIsNotBlank = !romVariationSource.trim().isEmpty();
        boolean translationIsNotBlank = !romVariationReplacement.trim().isEmpty();
        return tokenIsNotBlank && translationIsNotBlank;
    }

    public boolean isExtraWhiteSpacesMapping() {
        String replacementWithoutWhiteSpaces = romVariationReplacement.replace(SPACE, EMPTY);
        return !isLiteralMappingIneffective() && replacementWithoutWhiteSpaces.equals(romVariationSource);
    }

    private String literalReplace(String romName) {
        if (isLiteralMappingIneffective()) {
            return romName;
        }
        return romName.replace(romVariationSource, romVariationReplacement);
    }

    private String regexReplace(String romName) {
        if (isRegexMappingIneffective()) {
            return romName;
        }
        String newRomName = romName.replaceFirst(romVariationSource, romVariationReplacement);
        return Objects.equals(romName, newRomName) ? newRomName : regexReplace(newRomName);
    }

    private boolean isLiteralMappingIneffective() {
        return Objects.equals(romVariationSource, romVariationReplacement);
    }

    private boolean isRegexMappingIneffective() {
        return Objects.equals("$0", romVariationReplacement);
    }

    private String literalRemove(String romName) {
        return romName.replace(romVariationSource, EMPTY);
    }

    private String regexRemove(String romName) {
        return romName.replaceAll(romVariationSource, EMPTY);
    }

    private Stream<String> extractRegions(String romName) {
        if (romName.contains(romVariationSource)) {
            return Arrays.stream(romVariationReplacement.split(REGION_SEPARATOR));
        }
        return Stream.empty();
    }
}
