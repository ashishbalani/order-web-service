package com.interview.round1.ap124558.orderwebservice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class OrderDeserializer implements Deserializer<Order> {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Order deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            System.out.println("Deserializing...");
            return objectMapper.readValue(new String(data, "UTF-8"), Order.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to Order");
        }
    }

    @Override
    public void close() {

    }
}
