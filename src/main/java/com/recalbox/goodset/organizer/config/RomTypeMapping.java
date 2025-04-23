package com.recalbox.goodset.organizer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RomTypeMapping {
    private final String romTypeToken;
    private final String romTypeTranslation;
}
