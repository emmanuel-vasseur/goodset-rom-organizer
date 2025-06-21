package com.recalbox.goodset.organizer.config;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.recalbox.goodset.organizer.util.UncheckedIOExceptionThrower.rethrowIOException;

public class ConfigProperties {
    private static final String GAME_IMAGES_DIRECTORY_PROPERTY = "game-images.directory";
    private static final String FOLDER_IMAGES_DIRECTORY_PROPERTY = "folder-images.directory";
    private static final String LOWER_QUALITY_ROM_SUB_DIRECTORY_PROPERTY = "lower-quality-rom.directory";
    private static final String GAMELIST_FILENAME_PROPERTY = "gamelist.filename";
    private static final String REGION_ORDER_PREFERENCE_PROPERTY = "region.order-preference";

    private final Properties properties = new Properties();

    public ConfigProperties() {
        rethrowIOException(() -> properties.load(ConfigProperties.class.getResourceAsStream("/config.properties")));
    }

    public String getGameListFilename() {
        return properties.getProperty(GAMELIST_FILENAME_PROPERTY);
    }

    public String getLowerQualityRomsDirectory() {
        return properties.getProperty(LOWER_QUALITY_ROM_SUB_DIRECTORY_PROPERTY);
    }

    public String getGameImageDirectory() {
        return properties.getProperty(GAME_IMAGES_DIRECTORY_PROPERTY);
    }

    public String getFolderImageDirectory() {
        return properties.getProperty(FOLDER_IMAGES_DIRECTORY_PROPERTY);
    }

    public List<String> getRegionsPreferenceOrder() {
        return Arrays.asList(properties.getProperty(REGION_ORDER_PREFERENCE_PROPERTY).split(","));
    }

    public String replaceProperties(String template) {
        return template
                .replace("${" + GAME_IMAGES_DIRECTORY_PROPERTY + "}", getGameImageDirectory())
                .replace("${" + FOLDER_IMAGES_DIRECTORY_PROPERTY + "}", getFolderImageDirectory())
                .replace("${" + LOWER_QUALITY_ROM_SUB_DIRECTORY_PROPERTY + "}", getLowerQualityRomsDirectory());
    }
}
