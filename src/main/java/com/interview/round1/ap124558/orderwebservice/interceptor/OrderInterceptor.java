package com.interview.round1.ap124558.orderwebservice.interceptor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Setter
@Getter
@NoArgsConstructor
public class OrderInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtDecoder jwtDecoder;

    private Logger log = LoggerFactory.getLogger(OrderInterceptor.class);

    private static final String AUTH_HEADER = "authorization";
    private static final String SIMPLE_MESSAGE_TYPE = "simpleMessageType";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = getAccessor(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            final String authToken = accessor.getFirstNativeHeader(AUTH_HEADER);
            Jwt jwt = decode(authToken);
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            Authentication authentication = converter.convert(jwt);
            if (authentication.isAuthenticated())
                accessor.setUser(authentication);
            else
                throw new AuthenticationServiceException("Failed authentication with the jwt token " + jwt.toString());
        }
        return message;
    }

    public Jwt decode(String authToken) {
        return jwtDecoder.decode(authToken);
    }

    public StompHeaderAccessor getAccessor(@NonNull Message<?> message) {
        return MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    }

//    @Override
//    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if(null != accessor && accessor.getHeader(SIMPLE_MESSAGE_TYPE) == SimpMessageType.SUBSCRIBE) {
//
//        }
//
//    }
}
