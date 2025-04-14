package com.goodset.organizer.rom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RomTypeFileMapping {
    private final String romTypeExpression;
    private final String romTypeReplacement;
}
