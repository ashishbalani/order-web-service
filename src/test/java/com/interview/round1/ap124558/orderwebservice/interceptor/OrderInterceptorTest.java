package com.interview.round1.ap124558.orderwebservice.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderInterceptorTest {


    @Test
    void testPreSendConnect() {
        //Arrange
        Message<?> message = mock(Message.class);
        OrderInterceptor interceptorSpy = mock(OrderInterceptor.class);
        Jwt jwt = mock(Jwt.class);
        MessageChannel messageChannel = mock(MessageChannel.class);
        StompHeaderAccessor headerAccessor = mock(StompHeaderAccessor.class);

        when(interceptorSpy.decode("user")).thenReturn(jwt);
        when(interceptorSpy.getAccessor(message)).thenReturn(headerAccessor);
        when(headerAccessor.getCommand()).thenReturn(StompCommand.CONNECT);
        when(headerAccessor.getFirstNativeHeader(anyString())).thenReturn("user");
        when(interceptorSpy.preSend(message, messageChannel)).thenCallRealMethod();
        when(jwt.getClaimAsString(anyString())).thenReturn("ggsd");

        //Act
        interceptorSpy.preSend(message, messageChannel);

        //Verify
        verify(headerAccessor).setUser(any(JwtAuthenticationToken.class));
    }
}
