package com.arulvakku.app.event;

public class PlayerErrorEvent {
    public final String message;

    public PlayerErrorEvent(String message) {
        this.message = message;
    }
}
