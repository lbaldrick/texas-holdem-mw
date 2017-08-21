package com.baldrick.texas.holdem.model;

import com.baldrick.texas.holdem.statemachine.State;

public class TexasHoldemStateChange {
    private final Player player;
    
    private final State state;
    
    public TexasHoldemStateChange(State state, Player player) {
        this.state = state;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public State getState() {
        return state;
    }
}
