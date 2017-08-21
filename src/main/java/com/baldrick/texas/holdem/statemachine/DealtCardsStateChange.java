package com.baldrick.texas.holdem.statemachine;

import com.baldrick.texas.holdem.model.Card;
import java.util.List;


public class DealtCardsStateChange implements StateChange<List<Card>> {
    
    private final State state;
    
    private final List<Card> cards;
    
    private final String id;

    public DealtCardsStateChange(State state, List<Card> cards) {
        this.state = state;
        this.cards = cards;
        this.id = "DEALER";
    }
    
     public DealtCardsStateChange(State state, List<Card> cards, String id) {
        this.state = state;
        this.cards = cards;
        this.id = id;
    }

    @Override
    public State getState() {
        return state;
    }
    
    @Override
    public List<Card> getNextStateValue() {
        return cards;
    }

    @Override
    public String getId() {
        return id;
    }
}
