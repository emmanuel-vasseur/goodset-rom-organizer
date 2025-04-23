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

    public List<String> getFilenameUnknownRomTypes(String romName) {
        String cleanedRomName = removeFilenameKnownRomTypes(romName);
        return getRemainingRomTypes(cleanedRomName);
    }

    private String removeFilenameKnownRomTypes(String romName) {
        String cleanedRomName = romName;
        for (RomTypeMapping mapping : RomTypeLoader.FILENAME_ROM_TYPE_MAPPINGS) {
            cleanedRomName = cleanedRomName.replace(mapping.getRomTypeToken(), "");
        }
        for (RomTypeMapping mapping : RomTypeLoader.FILENAME_ROM_TYPE_REGEX_MAPPINGS) {
            cleanedRomName = cleanedRomName.replaceAll(mapping.getRomTypeToken(), "");
        }
        return cleanedRomName;
    }

    public String replaceFilenameRomTypes(String romName) {
        String newRomName = romName;
        for (RomTypeMapping mapping : RomTypeLoader.FILENAME_ROM_TYPE_MAPPINGS) {
            newRomName = newRomName.replace(mapping.getRomTypeToken(), mapping.getRomTypeTranslation());
        }
        for (RomTypeMapping mapping : RomTypeLoader.FILENAME_ROM_TYPE_REGEX_MAPPINGS) {
            newRomName = newRomName.replaceAll(mapping.getRomTypeToken(), mapping.getRomTypeTranslation());
        }
        return newRomName;
    }

    public List<String> getGamelistUnknownRomTypes(String romName) {
        String cleanedRomName = removeGamelistKnownRomTypes(romName);
        return getRemainingRomTypes(cleanedRomName);
    }

    public String replaceGamelistRomTypes(String romName) {
        String newRomName = romName;
        for (RomTypeMapping mapping : RomTypeLoader.GAMELIST_ROM_TYPE_MAPPINGS) {
            newRomName = newRomName.replace(mapping.getRomTypeToken(), mapping.getRomTypeTranslation());
        }
        for (RomTypeMapping mapping : RomTypeLoader.GAMELIST_ROM_TYPE_REGEX_MAPPINGS) {
            newRomName = newRomName.replaceAll(mapping.getRomTypeToken(), mapping.getRomTypeTranslation());
        }
        return newRomName;
    }

    private String removeGamelistKnownRomTypes(String romName) {
        String cleanedRomName = romName;
        for (RomTypeMapping mapping : RomTypeLoader.GAMELIST_ROM_TYPE_MAPPINGS) {
            cleanedRomName = cleanedRomName.replace(mapping.getRomTypeToken(), "");
        }
        for (RomTypeMapping mapping : RomTypeLoader.GAMELIST_ROM_TYPE_REGEX_MAPPINGS) {
            cleanedRomName = cleanedRomName.replaceAll(mapping.getRomTypeToken(), "");
        }
        return cleanedRomName;
    }

    private List<String> getRemainingRomTypes(String cleanedRomName) {
        Matcher romTypeMatcher = Pattern.compile("([(\\[][^)\\]]+[)\\]])").matcher(cleanedRomName);
        List<String> unknownRomTypes = new ArrayList<>();
        while (romTypeMatcher.find()) {
            unknownRomTypes.add(romTypeMatcher.group(1));
        }
        return unknownRomTypes;
    }
}
