package com.baldrick.texas.holdem.services;

import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Hand;
import com.baldrick.texas.holdem.model.Player;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary service, needs implemented properly
 */
@Service
public class LoginService {

    private ConcurrentHashMap<String, Player> loggedInPlayers = new ConcurrentHashMap<>();

    private int playerId = 0;

    public Optional<Player> getLoggedInPlayer(String username) {
        return Optional.ofNullable(loggedInPlayers.get(username));
    }

    public void loginPlayer(String username, String password) {
        loggedInPlayers.put(username, Player.newInstance("playerId" + ++playerId, username, 0.0, Hand.newInstance(), PlayerStatus.JOINING_TABLE));
    }
}
