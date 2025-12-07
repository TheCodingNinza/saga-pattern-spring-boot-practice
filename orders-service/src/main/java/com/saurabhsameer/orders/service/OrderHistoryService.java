package com.saurabhsameer.orders.service;

import com.saurabhsameer.core.types.OrderStatus;
import com.saurabhsameer.orders.dto.OrderHistory;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryService {
    void add(UUID orderId, OrderStatus orderStatus);

    List<OrderHistory> findByOrderId(UUID orderId);
}
