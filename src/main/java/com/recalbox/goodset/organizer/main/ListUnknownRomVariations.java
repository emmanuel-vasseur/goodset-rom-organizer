package com.recalbox.goodset.organizer.main;

import com.recalbox.goodset.organizer.GameListTransformer;
import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Log
public class ListUnknownRomVariations {

    private final RomOrganizer romOrganizer;

    public void listUnknownRomVariationsInFilenames() {
        Stream<String> romNames = FileUtils.listFiles(romOrganizer.romDirectory).stream().map(rom -> rom.getFileName().toString());
        Map<String, List<String>> romNamesWithUnknownVariations = romOrganizer.romNameHandling.getRomNamesWithUnknownRomVariations(romNames, romOrganizer.romNameHandling::getFilenameUnknownRomVariations);
        logRomNamesWithUnknownRomVariations(romNamesWithUnknownVariations, romOrganizer.romDirectory);
    }

    public void listUnknownRomVariationsInGameList() {
        GameListTransformer gameListTransformer = romOrganizer.createGameListTransformer();
        Map<String, List<String>> romNamesWithUnknownVariations = gameListTransformer.getGameListRomNamesWithUnknownRomVariations();
        logRomNamesWithUnknownRomVariations(romNamesWithUnknownVariations, romOrganizer.gameListFile);
    }

    private void logRomNamesWithUnknownRomVariations(Map<String, List<String>> romNamesWithUnknownVariations, Path location) {
        logMapOfList(romNamesWithUnknownVariations, location, "roms with unknown rom types", "unknown types");
        Map<String, List<String>> unknownRomVariationsWithRomNames = inverseMapOfList(romNamesWithUnknownVariations);
        logMapOfList(unknownRomVariationsWithRomNames, location, "unknown rom types", "roms");
    }

    private void logMapOfList(Map<String, List<String>> mapOfList,
                              Path location, String title, String mapEntryTitle) {
        if (mapOfList.isEmpty()) {
            log.info(String.format("** Result OK, No %s found in '%s' **", title, location));
        } else {
            log.warning(String.format("******** %s %s found in '%s' ********",
                    mapOfList.size(), title, location));
            mapOfList.forEach((entryKey, entryValues) ->
                    log.warning(String.format("%s %s - %s : %s",
                            entryValues.size(), mapEntryTitle, entryKey, String.join(", ", entryValues))));
        }
    }

    private Map<String, List<String>> inverseMapOfList(Map<String, List<String>> mapOfList) {
        return mapOfList.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream() // transform Entry<String, List<String>> to List<Entry<String, String>>
                        .map(value -> new AbstractMap.SimpleEntry<>(entry.getKey(), value)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue, // value becomes the key
                        TreeMap::new, // sort result
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList()) // group associated keys in list
                ));
    }
}
