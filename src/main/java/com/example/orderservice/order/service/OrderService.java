package com.example.orderservice.order.service;

import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.entity.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);


}
