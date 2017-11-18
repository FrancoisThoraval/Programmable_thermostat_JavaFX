package com.seekdasky.websocket;

public class WebSocketServer implements Runnable {

//    public final static String servName = "localhost:8080";
//    public final static String origin = "http://localhost";
    private static WebSocketServer _Instance = null;

    private final java.util.ArrayList<Client> _clients = new java.util.ArrayList<>();
    private final java.util.ArrayList<WebSocketListener> _listeners = new java.util.ArrayList<>();
    private java.net.ServerSocket _server_socket;

    public static WebSocketServer getInstance() {
        if (_Instance == null) {
            _Instance = new WebSocketServer();
        }
        return _Instance;
    }

    public static void printCopyrightToConsole() {
        System.out.println("WebSocket ver. 0.1 by SeekDaSky/xdrm-brackets");
    }

    synchronized public void addListener(WebSocketListener listener) {
        _listeners.add(listener);
    }

    synchronized public void removeListener(WebSocketListener listener) {
        _listeners.remove(listener);
    }

    @Override
    public void run() {
        while (!_server_socket.isClosed()) {
            try {
                System.out.println("Client is connecting...");
                Client client = new Client(_server_socket.accept());
                _clients.add(client);
                new Thread(client).start();
            } catch (java.io.IOException ioe) {
                //   ioe.printStackTrace();
                System.out.println("Server no longer available...");
            }
        }
    }

    public boolean start() {
        System.out.println("Server has started on 127.0.0.1:8080\r\nWaiting for a connection...");
        try {
            _server_socket = new java.net.ServerSocket(8080);
            new Thread(this).start();
            return true;
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    synchronized public void stop() {
        System.out.println("Server is stopping...");
        try {
            for (Client client : _clients) { // It does not occur since 'stop' is called when '_clients.isEmpty() == true'
                client.stop("shut down");
            }
            _server_socket.close();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void sendToken(Client client, Token token) throws java.io.IOException {
        if (client._isStreaming && !token.isStreamToken()) {
            throw new java.io.IOException("The client is streaming, you must finalize the running stream to send simple tokens");
        }
        client._outputStream.write(token.getFrame());
        client._outputStream.flush();
    }

    public void dispatchEvent(Event event, Token token) {
        for (WebSocketListener listener : _listeners) {
            if (token._opCode == Token.OpCodes.CONNECTION_CLOSED) {
                listener.processClosed(event);
            } else {
                listener.processEvent(event, token);
            }
        }
    }

    public boolean isAlive() {
        return !_server_socket.isClosed();
    }

    public void onClientClose(Client client) {
        assert (client._socket.isClosed());
        _clients.remove(client);
        if (_clients.isEmpty()) {
            //stop();
            //FrançoisThoraval: Removed this because if client close tab and reopen it, he can't reconnect
        }
//        for (WebSocketListener listener : _listeners) {
//            listener.processClosed(new Event(client));
//        }
    }
}
