package com.recalbox.goodset.organizer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RomTranslationTypeMappings {
    @Getter
    private final String translationTypeRegex;
    private final List<String> replacementPatterns;
    private final Map<String, String> matcherGroupTranslations;

    public String replaceAllTranslationTypes(String romName) {
        Matcher translationTypeMatcher = Pattern.compile(translationTypeRegex).matcher(romName);
        String newRomName = romName;
        while (translationTypeMatcher.find()) {
            String translationTypeReplacement = buildTranslationTypeReplacement(translationTypeMatcher);
            newRomName = translationTypeMatcher.replaceFirst(translationTypeReplacement);
            translationTypeMatcher.reset(newRomName);
        }
        return newRomName;
    }

    private String buildTranslationTypeReplacement(Matcher translationTypeMatcher) {
        String replacementPattern = getReplacementPattern(translationTypeMatcher);
        return buildTranslationTypeReplacement(translationTypeMatcher, replacementPattern);
    }

    private String getReplacementPattern(Matcher translationTypeMatcher) {
        int currentMatchGroupCount = getCurrentMatchGroupCount(translationTypeMatcher);
        return replacementPatterns.get(currentMatchGroupCount);
    }

    private int getCurrentMatchGroupCount(Matcher translationTypeMatcher) {
        int groupCount = translationTypeMatcher.groupCount();
        while (translationTypeMatcher.group(groupCount) == null) {
            groupCount--;
        }
        return groupCount;
    }

    private String buildTranslationTypeReplacement(Matcher translationTypeMatcher, String replacementPattern) {
        String translationTypeReplacement = replacementPattern;
        for (int index = 1; index <= translationTypeMatcher.groupCount(); index++) {
            String matcherGroupValue = translationTypeMatcher.group(index);
            String tokenToReplace = "$" + index;
            String tokenValue = matcherGroupTranslations.getOrDefault(matcherGroupValue, tokenToReplace);
            translationTypeReplacement = translationTypeReplacement.replace(tokenToReplace, tokenValue);
        }
        return translationTypeReplacement;
    }
}
