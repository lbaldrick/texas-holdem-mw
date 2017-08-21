package com.baldrick.texas.holdem.statemachine;

public interface State <T, C>{
    public void process(T context);
    public void notify(T context, C changedState);
}
