package com.baldrick.texas.holdem.components.game;

import com.baldrick.texas.holdem.model.Card;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import java.util.ArrayList;
import java.util.List;

public class Game {
    
    private final Deck deck;
    
    private final Pot pot;
    
    private final List<Player> currentPlayers = new ArrayList<>();
    
    private final int maxNumPlayers;
    
    private int currentPlayerIndex = 0;
    
    private Game(Deck deck, Pot pot, int maxNumPlayers) {
        this.deck = deck;
        this.pot = pot;
        this.maxNumPlayers = maxNumPlayers;
    }
    
    public static Game newInstance(Deck deck, Pot pot, int maxNumPlayers) {
        return new Game(deck, pot, maxNumPlayers);
    }
    
    public synchronized boolean addPlayer(Player player) {
        if (currentPlayers.size() > maxNumPlayers) {
            return false;
        }
        currentPlayers.add(player);
        
        return true;
    }
    
    public Player getPlayer(int index) {
        return currentPlayers.get(index);
    }
    
    public int getNextPlayerIndex() {
        if (currentPlayerIndex == currentPlayers.size()) {
            currentPlayerIndex = 0;
        } else {
            currentPlayerIndex++;
        }
        return currentPlayerIndex;
    }
    
    public void removePlayer(Player player) {
        currentPlayers.remove(player);
    }
    
    public synchronized List<Player> getCurrentPlayers() {
        return currentPlayers;
    }
    
    public double addToPot(double amount) {
        return pot.addToPot(amount);
    }

    public Pot getPot() {
        return Pot.newInstance(pot.getBalance());
    }
    
    public Card dealCard() {
        return deck.dealCard();
    }
    
    public List<Card> dealCards(int num) {
        return deck.dealCards(num);
    }
    
    public void start() {
        deck.shuffle();
    }
    
    public void finish() {
        pot.resetPot();
    }
}
