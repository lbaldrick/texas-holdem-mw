package com.baldrick.texas.holdem.dtos;

import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Card;

import java.util.List;

public class PlayerDto {
    private final String username;

    private final double amount;

    private final List<Card> cards;

    private final PlayerStatus currentState;

    public PlayerDto(String username, double amount, List<Card> cards, PlayerStatus currentState) {
        this.username = username;
        this.amount = amount;
        this.cards = cards;
        this.currentState = currentState;
    }

    public String getUsername() {
        return username;
    }

    public double getAmount() {
        return amount;
    }

    public List<Card> getCards() {
        return cards;
    }

    public PlayerStatus getCurrentState() {
        return currentState;
    }
}
