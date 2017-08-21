package com.baldrick.texas.holdem.game;

import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.statemachine.TexasHoldemStateMachine;


public class GameManager {
    
    TexasHoldemStateMachine stateMachine;

    private GameManager(TexasHoldemStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
    
    public boolean addPlayer(Player player) {
        boolean addedPlayer = this.stateMachine.addPlayer(player);
        return addedPlayer;
    }    
    
    public void cancel() {
        
    }
    
    public static GameManager newInstance(TexasHoldemStateMachine stateMachine) {
       return new GameManager(stateMachine);
    }
   
}
