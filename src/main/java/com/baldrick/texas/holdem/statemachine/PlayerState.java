package com.baldrick.texas.holdem.statemachine;

import com.baldrick.texas.holdem.model.Card;
import java.util.List;

public class PlayerState {
        private final PlayerStatus playerStatus;
        private final String playerId;
        private final List<Card> cards;
        private final double amount;

        public PlayerState(PlayerStatus playerStatus, String playerId, List<Card> cards, double amount) {
            this.playerStatus = playerStatus;
            this.playerId = playerId;
            this.cards = cards;
            this.amount  = amount;
        }
        
        public PlayerStatus getPlayerStatus() {
            return playerStatus;
        }

        public String getPlayerId() {
            return playerId;
        }

        public List<Card> getCards() {
            return cards;
        }

        public double getAmount() {
            return amount;
        }
        
        
    }