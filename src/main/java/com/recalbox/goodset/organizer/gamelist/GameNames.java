package com.recalbox.goodset.organizer.gamelist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;


@Getter
@RequiredArgsConstructor(access = PACKAGE)
public class GameNames {
    private final List<GameName> gameNameList;
    private final GameName gameNameReference;

    public boolean gameNameReferenceHasHighestDistributionRatio() {
        if (gameNameList.size() == 1) {
            return true;
        }
        List<GameName> sortedGameNameList = getGameNamesStartedByReferenceSortedByDistributionRatio();
        return sortedGameNameList.get(0).getDistributionRatioInGame() >= sortedGameNameList.get(1).getDistributionRatioInGame();
    }

    public List<GameName> getGameNamesStartedByReferenceSortedByDistributionRatio() {
        List<GameName> sortedGameNames = getGameNameList().stream()
                .sorted(GameName.HIGHEST_DISTRIBUTION_RATIO_IN_GAME_COMPARATOR)
                .collect(Collectors.toList());
        sortedGameNames.remove(getGameNameReference());
        sortedGameNames.add(0, getGameNameReference());
        return sortedGameNames;
    }
}
