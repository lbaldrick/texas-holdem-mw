package com.baldrick.texas.holdem.states;

import com.baldrick.texas.holdem.model.Card;
import com.baldrick.texas.holdem.enums.DealtCardsStatus;

import java.util.List;

public class DealtCardsState {

    private final List<Card> cards;

    private final DealtCardsStatus status;

    private final String playerId;

    public DealtCardsState(List<Card> cards, DealtCardsStatus status, String playerId) {
        this.cards = cards;
        this.status = status;
        this.playerId = playerId;
    }

    public List<Card> getCards() {
        return cards;
    }

    public DealtCardsStatus getStatus() {
        return status;
    }

    public String getPlayerId() {
        return playerId;
    }
}
