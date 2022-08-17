package com.example.orderservice.order.messagequeue;

import com.example.orderservice.order.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto send(String topic, OrderDto orderDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, jsonString);

         return orderDto;
    }
}
