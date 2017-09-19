package com.baldrick.texas.holdem.components.rooms;

import com.baldrick.texas.holdem.components.game.Game;
import com.baldrick.texas.holdem.controllers.GameController;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import com.baldrick.texas.holdem.notifiers.Notifier;
import com.baldrick.texas.holdem.states.StateChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component
public class RoomsManager {

    private static final Logger logger = LogManager.getLogger(RoomsManager.class);

    private final ConcurrentHashMap<String, Room> rooms;

    private static final String ROOM_NAME_PREFIX = "ROOM_";

    private static long GAME_WAIT_PERIOD = 30000L;

    private static int MAX_NUM_PLAYERS = 8;

    private static int NUM_ROOMS = 6;

    @Autowired
    private RoomsManager(Notifier notifier) {
        ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

        for (int x = 0; x < NUM_ROOMS; x++) {
            Game game = Game.newInstance(Deck.newInstance(), Pot.newInstance(0.00), MAX_NUM_PLAYERS);
            String roomId = ROOM_NAME_PREFIX + x;
            rooms.put(roomId, new Room(game, notifier, GAME_WAIT_PERIOD, Executors.newSingleThreadExecutor(), roomId));
        }

        this.rooms = rooms;
    }

    public boolean addPlayerToTable(String tableId, Player player) {
        Room stateMachine = rooms.get(tableId);

        if (stateMachine != null) {
            logger.info("Adding player to table. username={}, tableId={}", player.getUsername(), tableId);
            return stateMachine.addPlayer(player);
        }

        return false;
    }

    public Room getRoomForPlayer(String tableId, String playerId) {
        if (this.rooms.get(tableId).isPlayerInGame(playerId)) {
            return this.rooms.get(tableId);
        }

        return null;
    }

}
