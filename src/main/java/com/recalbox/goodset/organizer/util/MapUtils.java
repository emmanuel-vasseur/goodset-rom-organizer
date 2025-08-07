package com.recalbox.goodset.organizer.util;

import com.recalbox.goodset.organizer.gamelist.Game;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class MapUtils {

    public static <K, V> Map<K, Set<V>> mergeMaps(Map<K, Set<V>> firstMap, Map<K, Set<V>> secondMap) {
        Map<K, Set<V>> mergeMap = new HashMap<>(firstMap);
        secondMap.forEach((key, value) ->
                mergeMap.merge(key, value, MapUtils::mergeSets));
        return mergeMap;
    }

    public static <K> Set<K> mergeSets(Set<K> firstSet, Set<K> secondSet) {
        return Stream.concat(firstSet.stream(), secondSet.stream()).collect(Collectors.toSet());
    }

    public static <K, V, C extends Collection<V>> Map<K, C> removeEntriesWithSingleElement(Map<K, C> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().size() != 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<Game, Set<String>> reverseMultiMap(Map<String, Set<Game>> map) {
        return map.entrySet().stream()
                .flatMap(entry ->
                        entry.getValue().stream().map(game -> new AbstractMap.SimpleEntry<>(game, entry.getKey())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
    }

    public static <K, V> Set<V> getValuesOfKeys(Collection<K> keys, Map<K, Set<V>> map) {
        return keys.stream()
                .filter(map::containsKey)
                .map(map::get)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
