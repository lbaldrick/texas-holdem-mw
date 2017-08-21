package com.baldrick.texas.holdem.model;

import com.baldrick.texas.holdem.enums.Suit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {
    
    private static final int NUM_CARDS_PER_SUIT = 13;
    
    private final List<Card> cards;
    
    private final int deckSize;
    
    private int current = 0;
    
    private Deck(List<Card> cards) {
        this.cards = cards;
        deckSize = cards.size();
    }
    
    public static Deck newInstance() {
        return new Deck(createCardDeck());
    }
    
    public Deck shuffle() {
        Collections.shuffle(cards);
        return this;
    }
    
    public Card dealCard() {
        if (current >= cards.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return cards.get(current++);
    }
    
    public List<Card> dealCards(int numOfCards) {
        List<Card> dealtCards = new ArrayList<>();
        
        for (int x = 0; x < numOfCards; x ++) {
            if(current >= cards.size()) {
                break;
            }
            
            dealtCards.add(dealCard());
        }
        
        return dealtCards;
    }
    
    public void resetDeck() {
        this.shuffle();
        this.current = 0;
    }

    public int getDeckSize() {
        return deckSize;
    }
    
    private static List<Card> createCardDeck() {
        List<Card> deck = new ArrayList<>();
        
        Arrays.asList(Suit.values()).forEach((suit) -> {
            for(int x = 1 ; x < NUM_CARDS_PER_SUIT + 1 ; x++) {
                 deck.add(new Card(x, suit));
            }
        });
        
        return deck;
    }
}
