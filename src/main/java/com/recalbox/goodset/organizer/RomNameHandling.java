package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.RomTypeLoader;
import com.recalbox.goodset.organizer.config.RomTypeMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class RomNameHandling {

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

    public String replaceRomTypes(String romName) {
        String newRomName = RomTypeLoader.ROM_TRANSLATION_TYPE_MAPPINGS.replaceAllTranslationTypes(romName);

        for (RomTypeMapping mapping : RomTypeLoader.ROM_TYPE_MAPPINGS) {
            newRomName = newRomName.replace(mapping.getTypeToken(), mapping.getTypeTranslation());
        }
        return newRomName;
    }

    public List<String> hasUnknownRomTypes(String romName) {
        String cleanedRomName = removeKnownRomTypes(romName);

        Matcher romTypeMatcher = Pattern.compile("[(\\[]([^(\\[]+)[)\\]]").matcher(cleanedRomName);
        List<String> unknownRomTypes = new ArrayList<>();
        while (romTypeMatcher.find()) {
            unknownRomTypes.add(romTypeMatcher.group(1));
        }
        return unknownRomTypes;
    }

    private String removeKnownRomTypes(String romName) {
        String translationTypeRegex = RomTypeLoader.ROM_TRANSLATION_TYPE_MAPPINGS.getTranslationTypeRegex();
        String cleanedRomName = romName.replaceAll(translationTypeRegex, "");

        for (RomTypeMapping mapping : RomTypeLoader.ROM_TYPE_MAPPINGS) {
            cleanedRomName = cleanedRomName.replace(mapping.getTypeToken(), "");
        }
        for (String romType : RomTypeLoader.NOT_REPLACED_ROM_TYPES) {
            cleanedRomName = cleanedRomName.replace(romType, "");
        }
        for (String romTypeRegex : RomTypeLoader.NOT_REPLACED_ROM_TYPES_REGEX) {
            cleanedRomName = cleanedRomName.replaceAll(romTypeRegex, "");
        }
        return cleanedRomName;
    }
}
