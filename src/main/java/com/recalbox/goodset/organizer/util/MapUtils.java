package com.recalbox.goodset.organizer.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collector;
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

    public static <K, V> Map<V, Set<K>> reverseSetMultiMap(Map<K, Set<V>> map) {
        return reverseMultiMap(map, Collectors.toSet());
    }

    public static <K, V> Map<V, List<K>> reverseListMultiMap(Map<K, List<V>> map) {
        return reverseMultiMap(map, Collectors.toList());
    }

    private static <K, V, C extends Collection<K>> Map<V, C> reverseMultiMap(Map<K, ? extends Collection<V>> map,
                                                                             Collector<K, ?, C> valueCollector) {
        return map.entrySet().stream()
                .flatMap(entry ->
                        entry.getValue().stream().map(value -> new AbstractMap.SimpleEntry<>(value, entry.getKey())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, valueCollector)));
    }

    public static <K, V> Set<V> getAllValues(Collection<K> keys, Map<K, Set<V>> map) {
        return keys.stream()
                .filter(map::containsKey)
                .map(map::get)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
