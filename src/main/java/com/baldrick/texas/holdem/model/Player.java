package com.baldrick.texas.holdem.model;

import com.baldrick.texas.holdem.enums.PlayerStatus;


public class Player {
    private final String playerId;
    private final String username;
    private double accountBalance;
    private final Hand hand;
    private PlayerStatus playerStatus;

    private Player(String playerId, String username, double accountBalance, Hand hand, PlayerStatus playerStatus) {
        this.playerId = playerId;
        this.username = username;
        this.accountBalance = accountBalance;
        this.hand = hand;
        this.playerStatus = playerStatus;
    }

    public static Player newInstance(String playerId, String username, double accountBalance, Hand hand, PlayerStatus playerStatus) {
       return new Player(playerId, username, accountBalance, hand, playerStatus);
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public Hand getHand() {
        return hand;
    }
    
    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }
}
