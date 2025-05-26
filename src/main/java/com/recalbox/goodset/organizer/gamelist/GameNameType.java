package com.recalbox.goodset.organizer.gamelist;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
enum GameNameType {
    GAMELIST(RomInfo::getName, RomInfo::getNameWithoutDecorations),
    ROMPATH(RomInfo::getFileName, RomInfo::getFileNameWithoutDecorations);

    final Function<RomInfo, String> romNameExtractor;
    final Function<RomInfo, String> romNameWithoutDecorationsExtractor;
}
