package com.imwenwen.studycode.controller;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")  // 接口路径 ws://localhost:8080/webSocket/userId;
public class WebSocket {

    private static ConcurrentHashMap<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();
    //实例一个session，这个session是websocket的session
    private Session session;

    //新增一个方法用于主动向客户端发送消息
    public static void sendMessage(Object message, String userId) {
        WebSocket webSocket = webSocketMap.get(userId);
        if (webSocket != null) {
            try {
                webSocket.session.getBasicRemote().sendText(JSON.toJSONString(message));
                System.out.println("【websocket消息】发送消息成功,用户=" + userId + ",消息内容" + message.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConcurrentHashMap<String, WebSocket> getWebSocketMap() {
        return webSocketMap;
    }

    public static void setWebSocketMap(ConcurrentHashMap<String, WebSocket> webSocketMap) {
        WebSocket.webSocketMap = webSocketMap;
    }

    //前端请求时一个websocket时
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        webSocketMap.put(userId, this);
        sendMessage("CONNECT_SUCCESS", userId);
        System.out.println("【websocket消息】有新的连接,连接id" + userId);
    }

    //前端关闭时一个websocket时
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        webSocketMap.remove(userId);
        System.out.println("【websocket消息】连接断开,总数:" + webSocketMap.size());
    }

    //前端向后端发送消息
    @OnMessage
    public void onMessage(String message) {
        if (!message.equals("ping")) {
            System.out.println("【websocket消息】收到客户端发来的消息:" + message);
        }
    }
}