package com.baldrick.texas.holdem.model;

import java.util.ArrayList;
import java.util.List;


public class Hand {
    private final List<Card> cards;
    
    private Hand(List<Card> cards) {
        this.cards = cards;
    }
    
    public static Hand newInstance() {
        return new Hand(new ArrayList<>());
    }
    
    public void clearHand() {
        cards.clear();
    }
    
    public void addCard(Card card) {
        cards.add(card);
    }
    
    public void addCards(List<Card> cards) {
        cards.addAll(cards);
    }
    
    
    public List<Card> getCards() {
        return cards;
    }
    
}
