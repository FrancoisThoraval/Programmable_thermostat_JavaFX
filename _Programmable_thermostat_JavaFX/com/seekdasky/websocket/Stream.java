package com.seekdasky.websocket;

public class Stream {

    private final Client _client;
    private boolean _finalized;
    private final java.util.ArrayList<Token> _tokens = new java.util.ArrayList<>();

    private boolean isFirstChunkSent;

    public Stream(Client client) {
        _client = client;
        this.isFirstChunkSent = false;
        _finalized = false;
    }

    public void sendChunk(String s) throws Exception {
        if (_finalized) {
            throw new Exception("Stream already finalized...");
        }
        Token token;
        if (this.isFirstChunkSent) {
            token = new Token(Token.OpCodes.STREAM, false);
        } else {
            _client._isStreaming = true;
            token = new Token(Token.OpCodes.TEXT, false);
            this.isFirstChunkSent = true;
        }
        token.setRawString(s);
        WebSocketServer.getInstance().sendToken(_client, token);
        _tokens.add(token);
    }

    public void finalization() throws java.io.IOException {
        if (!_finalized) {
            Token token = new Token(Token.OpCodes.STREAM);
            token.setRawString(" ");
            WebSocketServer.getInstance().sendToken(_client, token);
            _tokens.add(token);
        }
        _client._isStreaming = false;
        _finalized = true; // Not sure, it's correct?
    }

    public String getFullString() {
        String s = "";
        for (Token token : _tokens) {
            s += token.getRaw();
        }
        return s;
    }
}
