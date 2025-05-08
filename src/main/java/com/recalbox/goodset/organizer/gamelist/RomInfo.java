package com.recalbox.goodset.organizer.gamelist;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class RomInfo {
    private final List<String> gamelistLines;

    private final long gameId;
    private final String path;
    private final String name;
    private final String image;

    public List<String> getGamelistLines() {
        return new ArrayList<>(gamelistLines);
    }

    public String getKey() {
        return path; // path is unique, this is the path of the rom file on disk
    }

    public String getFileName() {
        return path.substring(path.lastIndexOf('/') + 1);
    }

}
