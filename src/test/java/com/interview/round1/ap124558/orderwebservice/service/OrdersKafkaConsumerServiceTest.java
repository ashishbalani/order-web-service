package com.interview.round1.ap124558.orderwebservice.service;

import com.interview.round1.ap124558.orderwebservice.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class OrdersKafkaConsumerServiceTest {

    @InjectMocks
    private OrdersKafkaConsumerService ordersKafkaConsumerService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Captor
    private ArgumentCaptor<String> webSocketTopicNameCapture;

    @Captor
    private ArgumentCaptor<List<Order>> orderCapture;

    @Test
    void testConsumeOrderFromTopic() {
        //Arrange
        Order order = new Order();
        order.setOrderId(124441L);
        List<Order> orders = Collections.singletonList(order);

        //Act
        ordersKafkaConsumerService.consumeOrderFromTopic(orders);

        //Assert
        Mockito.verify(simpMessagingTemplate).convertAndSend(webSocketTopicNameCapture.capture(), orderCapture.capture());
        assertEquals("/topic/order",webSocketTopicNameCapture.getValue());
        assertEquals(1, orderCapture.getValue().size());
        assertEquals(orders, orderCapture.getValue());
    }
}
