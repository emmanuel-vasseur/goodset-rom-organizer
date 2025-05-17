package com.recalbox.goodset.organizer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public class RomVariationMapping {
    private final String romVariationSource;
    private final String romVariationReplacement;

    public boolean canBeApplied() {
        boolean tokenIsNotBlank = !romVariationSource.trim().isEmpty();
        boolean translationIsNotBlank = !romVariationReplacement.trim().isEmpty();
        return tokenIsNotBlank && translationIsNotBlank;
    }

    public boolean isExtraWhiteSpacesMapping() {
        String replacementWithoutWhiteSpaces = romVariationReplacement.replace(" ", "");
        return !isLiteralMappingIneffective() && replacementWithoutWhiteSpaces.equals(romVariationSource);
    }

    public String literalReplace(String romName) {
        if (isLiteralMappingIneffective()) {
            return romName;
        }
        return romName.replace(romVariationSource, romVariationReplacement);
    }

    public String regexReplace(String romName) {
        if (isRegexMappingIneffective()) {
            return romName;
        }
        String newRomName = romName.replaceAll(romVariationSource, romVariationReplacement);
        return Objects.equals(romName, newRomName) ? newRomName : regexReplace(newRomName);
    }

    private boolean isLiteralMappingIneffective() {
        return Objects.equals(romVariationSource, romVariationReplacement);
    }

    private boolean isRegexMappingIneffective() {
        return Objects.equals("$0", romVariationReplacement);
    }

    public String literalRemove(String romName) {
        return romName.replace(romVariationSource, "");
    }

    public String regexRemove(String romName) {
        return romName.replaceAll(romVariationSource, "");
    }

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

    private static String applyAll(String romName, List<RomVariationMapping> romVariationMappings, BiFunction<RomVariationMapping, String, String> operation) {
        String newRomName = romName;
        for (RomVariationMapping mapping : romVariationMappings) {
            newRomName = operation.apply(mapping, newRomName);
        }
        return newRomName;
    }
}
