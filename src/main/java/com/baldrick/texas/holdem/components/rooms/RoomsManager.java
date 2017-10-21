package com.baldrick.texas.holdem.components.rooms;

import com.baldrick.texas.holdem.components.game.Game;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import com.baldrick.texas.holdem.model.RoomDetails;
import com.baldrick.texas.holdem.notifiers.Notifier;
import com.baldrick.texas.holdem.services.GameService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Component
public class RoomsManager {

    private static final Logger logger = Logger.getLogger(RoomsManager.class);
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
            logger.info("Adding player to table. username=" + player.getUsername() + "tableId=" + tableId);
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

    public List<Player> getAllPlayersAtTable(String tableId) {
        Room room = rooms.get(tableId);

        return room.getAllPlayers();

    }

    public List<RoomDetails> getRooms() {
        List<RoomDetails> roomDetailsList =  new ArrayList<>();
        rooms.entrySet().forEach((room) -> roomDetailsList.add(room.getValue().getRoomDetails()));
        return roomDetailsList;
    }



}
