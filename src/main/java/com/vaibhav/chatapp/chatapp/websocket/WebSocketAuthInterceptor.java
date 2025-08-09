package com.vaibhav.chatapp.chatapp.websocket;

import com.vaibhav.chatapp.chatapp.model.User;
import com.vaibhav.chatapp.chatapp.security.jwt.JwtTokenProvider;
import com.vaibhav.chatapp.chatapp.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = servletRequest.getParameter("token");

        if (token != null) {
            Claims claims = jwtTokenProvider.validateAndGetClaims(token);
            User user = userService.findUserByPhoneNumber(claims.getSubject());

            if (user != null) {
                attributes.put("principal", new UsernamePasswordAuthenticationToken(user.getPhoneNumber(), null, List.of()));
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {}
}

