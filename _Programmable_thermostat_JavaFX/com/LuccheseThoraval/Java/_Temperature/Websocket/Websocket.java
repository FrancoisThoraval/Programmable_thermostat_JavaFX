/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LuccheseThoraval.Java._Temperature.Websocket;

import com.pauware.pauware_engine._Core.AbstractStatechart_monitor;
import com.seekdasky.websocket.*;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import static javax.json.Json.createObjectBuilder;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;

/**
 *
 * @author Francois
 */
public class Websocket implements com.pauware.pauware_engine._Core.AbstractStatechart_monitor_listener, com.seekdasky.websocket.WebSocketListener {

    private static WebSocketServer _WebSocketServer = null;
    private static Client _client = null;

    static {
        _WebSocketServer.printCopyrightToConsole();
        _WebSocketServer = com.seekdasky.websocket.WebSocketServer.getInstance();

        _WebSocketServer.start();
    }

    public JsonObjectBuilder createMessage(String action,String[] message) {
        int i = 0;
        JsonObjectBuilder json = createObjectBuilder();
        json.add("action", action);
        while(message[i] != null && i< message.length){
            String field = "value" + i;
            System.out.println(field);
            json.add(field, message[i]);
            i++;
        }
        return json;

    }

    @Override
    public void post_construct(AbstractStatechart_monitor as) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("post_construct");
    }

    @Override
    public void start(String string) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("start");
        System.out.println(string);
        _WebSocketServer.addListener(this);

    }

    @Override
    public void run_to_completion(String string, Hashtable hshtbl) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("run_to_completion");
    
    }

    @Override
    public void stop(String string) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("stop");
    }

    @Override
    public void pre_destroy() throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("pre_destroy");
    }

    @Override
    public void processEvent(Event event, Token token) {
        if (_client == null) {
            _client = event.getConnector();
            System.out.println("\n****Connection established****\n");
        }
        System.out.println("action: " + token.getString("action"));
    }

    @Override
    public void processClosed(Event event) {
        System.out.println("\n\n\n process closed ! \n\n\n");
    }
    
    public WebSocketServer getServer(){
        return Websocket._WebSocketServer;
    }

    public Client getClient() {
        return Websocket._client;
    }
    
}
