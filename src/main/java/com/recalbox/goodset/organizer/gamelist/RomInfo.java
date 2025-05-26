package com.recalbox.goodset.organizer.gamelist;

import com.recalbox.goodset.organizer.config.RomVariationMappingsLoader;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.regex.Pattern;

@Builder
@Getter
public class RomInfo {
    private static final String BRACKET_DECORATION_REGEX = "\\[[^]]+]";
    private static final String PARENTHESIS_DECORATION_REGEX = "\\([^)]+\\)";
    private static final Pattern DECORATIONS_PATTERN = Pattern.compile(
            "(\\s(" + BRACKET_DECORATION_REGEX + "|" + PARENTHESIS_DECORATION_REGEX + ")*+)*+$");

    private final int gameId;
    private final String path; // key of RomInfo, unique, path of the rom file on disk
    private final String name;
    private final String image;

    public String getNameWithoutDecorations() {
        return DECORATIONS_PATTERN.matcher(name).replaceAll("");
    }

    public String getFileNameWithoutDecorations() {
        return DECORATIONS_PATTERN.matcher(getFileNameWithoutExtension()).replaceAll("");
    }

    public String getFileNameWithoutExtension() {
        String fileName = getFileName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public String getFileName() {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public Set<String> getRegions() {
        return RomVariationMappingsLoader.extractRegions(name);
    }

}
