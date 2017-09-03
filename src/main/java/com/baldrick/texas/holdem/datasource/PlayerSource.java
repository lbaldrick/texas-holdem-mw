package com.baldrick.texas.holdem.datasource;

import com.baldrick.texas.holdem.model.Player;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerSource implements DataSource<Player> {

    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();

    @Override
    public Player getById(String id) {
        return players.get(id);
    }

    @Override
    public void deleteById(String id) {
        Player player = players.get(id);
        players.remove(player);
    }

    @Override
    public void update(String id, Player player) {
        if (players.containsKey(id)) {
            players.put(id, player);
        }
    }

    @Override
    public void insert(String id, Player player) {

        players.put(id, player);
    }
}
