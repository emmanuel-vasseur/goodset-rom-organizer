package com.recalbox.goodset.organizer.gamelist;

import com.recalbox.goodset.organizer.gamelist.RomInfo.RomInfoBuilder;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

@Log
public class GameListRomIterator implements Iterator<RomInfo> {
    private static final Pattern GAME_XML_START_TAG_PATTERN = Pattern.compile("^\\s*<game id=\"(\\d+)\" .*>\\s*$");
    private static final Pattern GAME_XML_END_TAG_PATTERN = Pattern.compile("^\\s*</game>\\s*$");
    private static final Pattern GAME_ATTRIBUTE_PATTERN = Pattern.compile("^\\s*<(\\w+)>(.*)</(\\1)>\\s*$");

    private final Iterator<String> gameListContent;

    private Integer gameId = null;
    private List<String> gameLines = null;
    private RomInfoBuilder romInfoBuilder = null;

    public GameListRomIterator(List<String> gameListContent) {
        this.gameListContent = new ArrayList<>(gameListContent).iterator();
    }

    @Override
    public boolean hasNext() {
        if (nonNull(romInfoBuilder)) {
            return true;
        }
        while (gameListContent.hasNext()) {
            String line = gameListContent.next();
            Matcher gameMatcher = GAME_XML_START_TAG_PATTERN.matcher(line);
            if (gameMatcher.matches()) {
                gameId = Integer.valueOf(gameMatcher.group(1));
                romInfoBuilder = RomInfo.builder();
                gameLines = new ArrayList<>();
                gameLines.add(line);
                return true;
            }
        }
        return false;
    }

    @Override
    public RomInfo next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more element to iterate over.");
        }

        while (gameListContent.hasNext()) {
            String line = gameListContent.next();
            gameLines.add(line);

            Matcher endGameMatcher = GAME_XML_END_TAG_PATTERN.matcher(line);
            if (endGameMatcher.matches()) {
                return createRomInfo();
            }

            Matcher gameAttributeMatcher = GAME_ATTRIBUTE_PATTERN.matcher(line);
            if (gameAttributeMatcher.matches()) {
                extractAttribute(gameAttributeMatcher.group(1), gameAttributeMatcher.group(2));
            }
        }

        log.warning("Last game of gamelist has no end tag");
        return createRomInfo();
    }

    private RomInfo createRomInfo() {
        RomInfo romInfo = romInfoBuilder
                .gameId(gameId)
                .build();
        gameId = null;
        gameLines = null;
        romInfoBuilder = null;
        return romInfo;
    }

    private void extractAttribute(String attributeTag, String attributeValue) {
        switch (attributeTag) {
            case "path":
                romInfoBuilder.path(attributeValue);
                break;
            case "name":
                romInfoBuilder.name(attributeValue);
                break;
            case "image":
                romInfoBuilder.image(attributeValue);
                break;
            default:
                // Do nothing
        }
    }
}
