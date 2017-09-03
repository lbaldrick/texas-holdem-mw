package com.baldrick.texas.holdem.states;


public class PlayerStateChange implements StateChange<PlayerState> {

    private final PlayerState playerState;

    private final String id;

    public PlayerStateChange(PlayerState playerState) {
        this.playerState = playerState;
        this.id = "PLAYER_STATE_CHANGE";
    }

    @Override
    public PlayerState getStateValue() {
        return playerState;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
}
