package com.saurabhsameer.orders.service.handler;

import com.saurabhsameer.core.dto.commands.ApproveOrderCommand;
import com.saurabhsameer.core.dto.commands.RejectOrderCommand;
import com.saurabhsameer.orders.service.OrderService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${orders.commands.topic.name}")
public class OrderCommandsHandler {

    private final OrderService orderService;

    public OrderCommandsHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaHandler
    public void handleCommand(@Payload ApproveOrderCommand approveOrderCommand){

        orderService.approveOrder(approveOrderCommand.getOrderId());

    }

    @KafkaHandler
    public void handleCommand(@Payload RejectOrderCommand rejectOrderCommand){
        orderService.rejectOrder(rejectOrderCommand.getOrderId());
    }

}
