package com.baldrick.texas.holdem.statemachine;

public interface StateChange<T> {
    
    public State getState();
    
    public T getNextStateValue();
    
    public String getId();
}
