package cn.org.alan.exam.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/websocket")
@Component
@Slf4j
public class WebsocketHandler {

    private static final ConcurrentHashMap<Integer, Session> SESSION_MAP = new ConcurrentHashMap<>();


    @Autowired
    public void setInstance() {

    }

    @OnOpen
    public void onOpen(Session session) {
        
        Integer userId = getUserIdBySession(session);
        if (Objects.nonNull(SESSION_MAP.get(userId)) && SESSION_MAP.get(userId).isOpen()) {
            
            return;
        }

        
        SESSION_MAP.put(userId, session);

        log.info("[websocket消息]：用户 {} 加入连接，当前连接总数：{}", userId, SESSION_MAP.size());
    }


    @OnClose
    public void onClose(Session session) {
        Integer userId = getUserIdBySession(session);
        Session existSession = SESSION_MAP.get(userId);
        if (Objects.isNull(existSession)) {
            
            return;
        }
        
        SESSION_MAP.remove(userId);


        log.info("[websocket消息]：用户 {} 断开连接", userId);
    }

    @OnError
    public void onError(Throwable throwable) {
        log.error("WebSocket error: {}", throwable.getMessage());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        
        

        
        sendAllMessage(message);
        log.info("[websocket消息]：收到消息 {}", message);

    }

    
    private void sendAllMessage(String message) {
        SESSION_MAP.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    synchronized (WebsocketHandler.class) {
                        session.getBasicRemote().sendText(message);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    
    private Integer getUserIdBySession(Session session) {
        String[] arr = session.getRequestURI().getQuery().split("=");
        return Integer.parseInt(arr[arr.length - 1]);
    }
}
