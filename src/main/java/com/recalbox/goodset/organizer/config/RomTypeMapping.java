package com.recalbox.goodset.organizer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RomTypeMapping {
    private final String typeToken;
    private final String typeTranslation;
}
