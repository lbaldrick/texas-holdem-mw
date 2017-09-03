package com.baldrick.texas.holdem.states;

public interface State <T, C>{
    public void process(T context);
    public void notify(T context, C changedState);
}
