package com.baldrick.texas.holdem.components.rooms;

import com.baldrick.texas.holdem.components.game.Game;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import com.baldrick.texas.holdem.states.StateChange;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RoomsManager {

    private final ConcurrentHashMap<String, Room> rooms;

    private static final String ROOM_NAME_PREFIX = "ROOM_";

    private static long GAME_WAIT_PERIOD = 3L;

    private static int MAX_NUM_PLAYERS = 8;

    private RoomsManager(ConcurrentHashMap<String, Room> rooms) {
        this.rooms = rooms;
    }

    public static RoomsManager newInstance(int numOfRooms, Consumer<StateChange> notifier) {
        ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

        for (int x = 0; x < numOfRooms; x++) {
            Game game = Game.newInstance(Deck.newInstance(), Pot.newInstance(0.00), MAX_NUM_PLAYERS);

            rooms.put(ROOM_NAME_PREFIX + x, new Room(game, notifier, GAME_WAIT_PERIOD, Executors.newSingleThreadExecutor()));
        }
        return new RoomsManager(rooms);
    }

    public boolean addPlayerToTable(String tableId, Player player) {
        Room stateMachine = rooms.get(tableId);

        if (stateMachine != null) {
            return stateMachine.addPlayer(player);
        }

        return false;
    }

    public Room getStateMachineForPlayer(String tableId, String playerId) {
        if (this.rooms.get(tableId).isPlayerInGame(playerId)) {
            return this.rooms.get(tableId);
        }

        return null;
    }

}
