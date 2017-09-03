package com.baldrick.texas.holdem.dtos;

public class WsMessage {

    private String payload;

    public WsMessage() {}

    public WsMessage(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
