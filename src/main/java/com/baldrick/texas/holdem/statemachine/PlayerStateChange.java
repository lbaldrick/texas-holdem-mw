package com.baldrick.texas.holdem.statemachine;


public class PlayerStateChange implements StateChange<PlayerState> {
    
    private final State state;
    
    private final PlayerState playerState;

    public PlayerStateChange(State state, PlayerState playerState) {
        this.state = state;
        this.playerState = playerState;
    }
    
    @Override
    public State getState() {
        return state;
    }

    @Override
    public PlayerState getNextStateValue() {
        return playerState;
    }
    
    @Override
    public String getId() {
        return playerState.getPlayerId();
    }
    
}
