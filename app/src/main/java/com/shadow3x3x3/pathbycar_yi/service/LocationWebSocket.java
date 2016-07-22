package com.shadow3x3x3.pathbycar_yi.service;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by ShengWei on 2016/7/22.
 * Using for sending location to websocket server
 */
public class LocationWebSocket extends WebSocketClient {
    public LocationWebSocket(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {

    }
}
