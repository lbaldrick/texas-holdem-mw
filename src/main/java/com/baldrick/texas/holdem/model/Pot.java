package com.baldrick.texas.holdem.model;

public class Pot {
    
    private double balance;
    
    private Pot(double balance) {
        this.balance = balance;
    }
    
    public static Pot newInstance(Double balance) {
        return new Pot(balance);
    }
    
    public double addToPot(double amount) {
        this.balance+=amount;
        return this.balance;
    }
    
    public void resetPot() {
        this.balance = 0.0;
    }

    public double getBalance() {
        return balance;
    }
}
