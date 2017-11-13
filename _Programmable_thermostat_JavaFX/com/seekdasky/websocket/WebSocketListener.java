package com.seekdasky.websocket;

public interface WebSocketListener {

    public void processEvent(Event event, Token token);

    public void processClosed(Event event);
}
