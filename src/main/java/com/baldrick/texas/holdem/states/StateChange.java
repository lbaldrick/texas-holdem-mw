package com.baldrick.texas.holdem.states;

public interface StateChange<T> {
    
    public T getStateValue();
    
    public String getId();
}
