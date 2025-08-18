package com.tech.snapbid.realtime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class StompEventsLogger {
    @EventListener
    public void onConnect(SessionConnectEvent e) {
        StompHeaderAccessor a = StompHeaderAccessor.wrap(e.getMessage());
        log.info("STOMP CONNECT sessionId={} headers={}", a.getSessionId(), a.toNativeHeaderMap());
    }
    @EventListener
    public void onSubscribe(SessionSubscribeEvent e) {
        StompHeaderAccessor a = StompHeaderAccessor.wrap(e.getMessage());
        log.info("STOMP SUBSCRIBE sessionId={} destination={} id={}",
                a.getSessionId(), a.getDestination(), a.getSubscriptionId());
    }
}