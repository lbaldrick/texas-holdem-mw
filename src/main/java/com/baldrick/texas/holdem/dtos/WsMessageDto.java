package com.baldrick.texas.holdem.dtos;

public class WsMessageDto {

    private String payload;

    public WsMessageDto(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
