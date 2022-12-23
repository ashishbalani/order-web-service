package com.interview.round1.ap124558.orderwebservice.integration;

import com.interview.round1.ap124558.orderwebservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationConfig.class)
public class WebSocketIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private Integer port;

    @Autowired
    private OAuth2AccessToken oAuth2AccessToken;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    void setup() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    void testConnection() throws InterruptedException, ExecutionException, TimeoutException {
        BlockingQueue<Order> blockingQueue = new ArrayBlockingQueue<>(1);
        StompSession session = webSocketStompClient.connect(
                String.format("ws://localhost:%d/order-websocket", port),
                new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        session.subscribe("/topic/order", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Order.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                headers.add("authorization", oAuth2AccessToken.getTokenValue());
                blockingQueue.add((Order) payload);
            }
        });
        Order order = new Order();
        order.setOrderId(14124L);
        session.send("/topic/order", order);
        await()
                .atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(order, blockingQueue.poll()));

    }

}
