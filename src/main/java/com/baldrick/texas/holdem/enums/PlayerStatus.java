package com.baldrick.texas.holdem.enums;


import java.util.List;
import static com.google.common.collect.Lists.newArrayList;

public enum PlayerStatus {
    JOINING_TABLE, FOLD, RAISE, BET, CHECK, LEFT_TABLE, HAS_FOCUS, WAITING, WINNER;

    private static final List<PlayerStatus> ACTIVE_STATUSES = newArrayList(RAISE, BET, WAITING);

    public boolean isActiveStatus(PlayerStatus status) {
        return ACTIVE_STATUSES.contains(status);
    }
}


