package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.RomVariationMapping;
import com.recalbox.goodset.organizer.config.RomVariationMappingsLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class RomNameHandling {

    public Map<String, List<String>> getRomNamesWithUnknownRomVariations(Stream<String> romNames, Function<String, List<String>> unknownRomVariationsExtractor) {
        Map<String, List<String>> romNamesWithUnknownTypes = romNames
                .collect(Collectors.toMap(
                        Function.identity(), // key: rom name
                        unknownRomVariationsExtractor, // value: list of unknown rom types
                        (a, b) -> a, // keep one of both if the same rom name exists in parameter (lists equal)
                        TreeMap::new) // sort result by rom name
                );
        romNamesWithUnknownTypes.values().removeIf(List::isEmpty);
        return romNamesWithUnknownTypes;
    }

    public boolean isGoodDump(String romName) {
        return romName.contains("[!]");
    }

    public String getGame(String romName) {
        return romName.replaceFirst("^([^(\\[]*)[(\\[].*$", "$1").trim();
    }

    public List<String> getLowerQualityRomNames(List<String> gameRomNames) {
        List<String> lowerQualityRoms = gameRomNames.stream()
                .filter(romName -> !this.isGoodDump(romName))
                .collect(Collectors.toList());

        boolean bestQualityRomsExist = lowerQualityRoms.size() != gameRomNames.size();
        boolean lowerQualityRomsExist = !lowerQualityRoms.isEmpty();
        if (lowerQualityRomsExist && bestQualityRomsExist) {
            return lowerQualityRoms;
        }
        return emptyList();
    }

    public List<String> getFilenameUnknownRomVariations(String romName) {
        String cleanedRomName = removeFilenameKnownRomVariations(romName);
        return getRemainingRomVariations(cleanedRomName);
    }

    private String removeFilenameKnownRomVariations(String romName) {
        String cleanedRomName = RomVariationMapping.removeLiterals(romName, RomVariationMappingsLoader.FILENAME_ROM_VARIATION_MAPPINGS);
        return RomVariationMapping.removeRegexMatches(cleanedRomName, RomVariationMappingsLoader.FILENAME_ROM_VARIATION_REGEX_MAPPINGS);
    }

    public String replaceFilenameRomVariations(String romName) {
        String newRomName = RomVariationMapping.replaceLiterals(romName, RomVariationMappingsLoader.FILENAME_ROM_VARIATION_MAPPINGS);
        return RomVariationMapping.replaceRegexMatches(newRomName, RomVariationMappingsLoader.FILENAME_ROM_VARIATION_REGEX_MAPPINGS);
    }

    public List<String> getGameListUnknownRomVariations(String romName) {
        String cleanedRomName = removeGameListKnownRomVariations(romName);
        return getRemainingRomVariations(cleanedRomName);
    }

    public String replaceGameListRomVariations(String romName) {
        String gameListRomName = RomVariationMapping.replaceLiterals(romName, RomVariationMappingsLoader.FILENAME_TO_GAMELIST_ROM_VARIATION_MAPPINGS);
        String newRomName = RomVariationMapping.replaceLiterals(gameListRomName, RomVariationMappingsLoader.GAMELIST_ROM_VARIATION_MAPPINGS);
        return RomVariationMapping.replaceRegexMatches(newRomName, RomVariationMappingsLoader.GAMELIST_ROM_VARIATION_REGEX_MAPPINGS);
    }

    private String removeGameListKnownRomVariations(String romName) {
        String gameListRomName = RomVariationMapping.replaceLiterals(romName, RomVariationMappingsLoader.FILENAME_TO_GAMELIST_ROM_VARIATION_MAPPINGS);
        String cleanedRomName = RomVariationMapping.removeLiterals(gameListRomName, RomVariationMappingsLoader.GAMELIST_ROM_VARIATION_MAPPINGS);
        return RomVariationMapping.removeRegexMatches(cleanedRomName, RomVariationMappingsLoader.GAMELIST_ROM_VARIATION_REGEX_MAPPINGS);
    }

    private List<String> getRemainingRomVariations(String cleanedRomName) {
        Matcher romVariationMatcher = Pattern.compile("([(\\[][^)\\]]+[)\\]])").matcher(cleanedRomName);
        List<String> unknownRomVariations = new ArrayList<>();
        while (romVariationMatcher.find()) {
            unknownRomVariations.add(romVariationMatcher.group(1));
        }
        return unknownRomVariations;
    }
}
