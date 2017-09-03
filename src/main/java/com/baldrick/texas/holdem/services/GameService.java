package com.baldrick.texas.holdem.services;

import com.baldrick.texas.holdem.components.rooms.RoomsManager;
import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.components.rooms.Room;

public class GameService {
    private final RoomsManager roomsManager;

    public GameService(RoomsManager roomsManager) {
        this.roomsManager = roomsManager;
    }

    public boolean addPlayerToTable(String tableId, Player player) {
        return roomsManager.addPlayerToTable(tableId, player);
    }

    public void performPlayerAction(PlayerStatus requestedStatus, String tableId, String playerId) {
        performPlayerAction(requestedStatus, tableId, playerId, 0.0);
    }

    private void performPlayerAction(PlayerStatus requestedStatus, String tableId, String playerId, double amount) {
        Room room = roomsManager.getStateMachineForPlayer(tableId, playerId);
        if (room != null) {
            switch(requestedStatus) {
                case BET: playerBet(playerId, amount, room); break;
                case FOLD: playerFold(playerId, room); break;
                case RAISE: playerRaise(playerId, amount, room); break;
                case CHECK: playerCheck(playerId, room); break;
                case LEFT_TABLE: playerLeftTable(playerId, room); break;
                default: break;
            }
        }
    }

    private void playerBet(String playerId, double amount, Room room) {
        room.playerBet(playerId, amount);
    }

    private void playerRaise(String playerId, double amount, Room room) {
        room.playerRaise(playerId, amount);
    }

    private void playerCheck(String playerId, Room room) {
        room.playerCheck(playerId);
    }

    private void playerFold(String playerId, Room room) {
        room.playerFold(playerId);
    }

    private void playerLeftTable(String playerId, Room room) {
        room.playerLeftTable(playerId);
    }
}
