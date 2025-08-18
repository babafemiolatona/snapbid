package com.tech.snapbid.realtime;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.tech.snapbid.config.JwtUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        if (acc.getCommand() == StompCommand.CONNECT) {
            String token = resolveToken(acc);
            
            if (token == null) {
                throw new MessagingException("Missing JWT");
            }
            
            String username;

            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                throw new MessagingException("Invalid JWT");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (!jwtUtil.validateToken(token, userDetails)) {
                throw new MessagingException("JWT validation failed");
            }
            
            Principal principal = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            acc.setUser(principal);
        }
        return message;
    }

    private String resolveToken(StompHeaderAccessor acc) {
        String raw = first(acc, "Authorization");
        if (raw == null) raw = first(acc, "authorization");
        if (raw == null) raw = first(acc, "token");
        if (raw == null) return null;
        if (raw.startsWith("Bearer ")) return raw.substring(7).trim();
        return raw.trim();
    }

    private String first(StompHeaderAccessor acc, String name) {
        List<String> vals = acc.getNativeHeader(name);
        return (vals == null || vals.isEmpty()) ? null : vals.get(0);
    }
}