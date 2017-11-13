package com.seekdasky.websocket;

public class Event extends java.util.EventObject {

    public Event(Client client) {
        super(client);
    }

    public Client getConnector() {
        return (Client) this.getSource();
    }
}
