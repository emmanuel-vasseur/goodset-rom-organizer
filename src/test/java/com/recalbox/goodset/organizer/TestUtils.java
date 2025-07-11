package com.recalbox.goodset.organizer;

import com.recalbox.goodset.organizer.config.RomVariationMappingsLoader;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class TestUtils {
    public static List<String> loadGameListLines(String name) {
        InputStream gameListInputStream = RomVariationMappingsLoader.class.getResourceAsStream(name);
        return FileUtils.readLines(gameListInputStream).collect(Collectors.toList());
    }
}
