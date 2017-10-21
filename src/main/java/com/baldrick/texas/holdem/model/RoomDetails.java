package com.baldrick.texas.holdem.model;

import java.util.List;

public class RoomDetails {

    private final String roomId;
    private final int totaNumOfSeats;
    private final List<String> playerIds;

    public RoomDetails(String roomId, int totaNumOfSeats, List<String> playerIds) {
        this.roomId = roomId;
        this.totaNumOfSeats = totaNumOfSeats;
        this.playerIds = playerIds;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getTotaNumOfSeats() {
        return totaNumOfSeats;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }
}
