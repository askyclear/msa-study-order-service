package com.example.orderservice.order.controller;

import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.entity.OrderEntity;
import com.example.orderservice.order.messagequeue.KafkaProducer;
import com.example.orderservice.order.service.OrderService;
import com.example.orderservice.order.vo.RequestOrder;
import com.example.orderservice.order.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController {
    private final ModelMapper modelMapper;
    private final OrderService orderService;
    private final Environment environment;
    private final KafkaProducer kafkaProducer;

    public OrderController(ModelMapper modelMapper, OrderService orderService, Environment environment, KafkaProducer kafkaProducer) {
        this.modelMapper = modelMapper;
        this.orderService = orderService;
        this.environment = environment;
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", environment.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@RequestBody RequestOrder order, @PathVariable String userId) {
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createdOrder = orderService.createOrder(orderDto);

        kafkaProducer.send(environment.getProperty("kafka.catalog-topic"), orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdOrder, ResponseOrder.class));

    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable String userId) {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();

        orderList.forEach((orderEntity -> result.add(modelMapper.map(orderEntity, ResponseOrder.class))));

        return ResponseEntity.ok(result);
    }
}
