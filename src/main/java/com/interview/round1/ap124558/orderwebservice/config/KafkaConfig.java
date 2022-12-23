package com.interview.round1.ap124558.orderwebservice.config;

import com.interview.round1.ap124558.orderwebservice.model.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Order> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        return factory;
    }

    private ConsumerFactory<String, Order> orderConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties());
    }

    private Map<String, Object> consumerProperties() {
       Map<String, Object> props = new HashMap<>();
       props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://localhost:9092");
       props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
       props.put(ConsumerConfig.GROUP_ID_CONFIG, "ap124558-orders-web-service");
       props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
       props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

       props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
       props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
       props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
               "com.interview.round1.ap124558.orderwebservice.model.Order");
       props.put(JsonDeserializer.TRUSTED_PACKAGES,"com.interview.round1.ap124558.orderwebservice.model");
       props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
       props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
       props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 52428800);
       props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 5000);

       return props;
    }
}
