package com.interview.round1.ap124558.orderwebservice.service;

import com.interview.round1.ap124558.orderwebservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdersKafkaConsumerService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger log = LoggerFactory.getLogger(OrdersKafkaConsumerService.class);

    @KafkaListener(topics = "${kafka.inbound.topic}", containerFactory = "kafkaListenerContainerFactory", concurrency = "2")
    public void consumeOrderFromTopic(List<Order> orders) {
        log.info("{} Order events received from Topic", orders);
        simpMessagingTemplate.convertAndSend("/topic/order", orders);
    }

}
