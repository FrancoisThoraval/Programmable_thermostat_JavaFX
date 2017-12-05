package com.seekdasky.websocket;

public class Client implements Runnable {

    protected boolean _isStreaming = false;
    protected final java.io.InputStream _inputStream;
    protected final java.io.OutputStream _outputStream;
    protected final java.net.Socket _socket;
    
    //François Thoraval:    J'ai ajouté un id pour potentiellement envoyer 
    //                      à un client précis certaines informations
    public int id;

    public Client(java.net.Socket socket) throws java.io.IOException {
        _inputStream = socket.getInputStream();
        _outputStream = socket.getOutputStream();
        _socket = socket;
        //
        id = -1;
    }

    public void stop(String reason) throws java.io.IOException {
        System.out.println("Client is stopping for reason: " + reason);
        Token token = new Token(Token.OpCodes.CONNECTION_CLOSED);
        token.setRawString(reason);
        WebSocketServer.getInstance().sendToken(this, token);
    }

    private void dispatchEvent(byte[] message) {
        java.util.ArrayList<byte[][]> tokens = Token.splitFrames(message);
        for (byte[][] stream : tokens) {
            for (byte[] frame : stream) {
                Token t = new Token(frame);
                if (t._opCode == Token.OpCodes.PING) {
                    try {
                        WebSocketServer.getInstance().sendToken(this, new Token(Token.OpCodes.PONG));
                    } catch (java.io.IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    if (t._opCode == Token.OpCodes.CONNECTION_CLOSED) {
                        try {
                            _socket.close();
                        } catch (java.io.IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    Event e = new Event(this);
                    WebSocketServer.getInstance().dispatchEvent(e, t);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            _initialization();
            while (!_socket.isClosed()) {
                byte[] message = new byte[_inputStream.available()];
                int i = _inputStream.read(message);
                if (i != 0) {
                    this.dispatchEvent(message);
                }
            }
//            _inputStream.close();
//            _outputStream.close();
        } catch (java.security.NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (java.io.IOException ioe) {
            // TODO Auto-generated catch block
            ioe.printStackTrace();
        }
        //WebSocketServer.getInstance().onClientClose(this);
    }

    private void _initialization() throws java.security.NoSuchAlgorithmException, java.io.UnsupportedEncodingException {
        String headers = new java.util.Scanner(_inputStream, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        java.util.regex.Matcher get = java.util.regex.Pattern.compile("^GET").matcher(headers);
        if (get.find()) {
            java.util.regex.Matcher keyMatch = java.util.regex.Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(headers);
            keyMatch.find();

            String secKey = javax.xml.bind.DatatypeConverter.printBase64Binary(
                    java.security.MessageDigest.getInstance("SHA-1")
                            .digest((keyMatch.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));

            String response = "HTTP/1.1 101 Switching Protocols\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Sec-WebSocket-Accept: " + secKey + "\r\n"
                    + "Sec-WebSocket-Protocol: PauWare_view \r\n"
                    + "\r\n";

            try {
                _outputStream.write(response.getBytes("UTF-8"), 0, response.getBytes("UTF-8").length);
                _outputStream.flush();
            } catch (java.io.IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
