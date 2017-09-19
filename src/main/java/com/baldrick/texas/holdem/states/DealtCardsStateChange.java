package com.baldrick.texas.holdem.states;


public class DealtCardsStateChange implements StateChange<DealtCardsState> {
    
    private final DealtCardsState dealtCardsState;
    
    private final String id;

    public DealtCardsStateChange(DealtCardsState dealtCardsState) {
        this.dealtCardsState = dealtCardsState;
        this.id = "DEALER_STATE_CHANGE";
    }

    @Override
    public DealtCardsState getStateValue() {
        return dealtCardsState;
    }

    @Override
    public String getId() {
        return id;
    }
}
