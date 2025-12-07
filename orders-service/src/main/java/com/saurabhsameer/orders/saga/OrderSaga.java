package com.saurabhsameer.orders.saga;

import com.saurabhsameer.core.dto.commands.ApproveOrderCommand;
import com.saurabhsameer.core.dto.commands.ProcessPaymentCommand;
import com.saurabhsameer.core.dto.commands.ReserveProductCommand;
import com.saurabhsameer.core.dto.events.OrderApprovedEvent;
import com.saurabhsameer.core.dto.events.OrderCreatedEvent;
import com.saurabhsameer.core.dto.events.PaymentProcessedEvent;
import com.saurabhsameer.core.dto.events.ProductsReservedEvent;
import com.saurabhsameer.core.types.OrderStatus;
import com.saurabhsameer.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.events.topic.name}",
        "${products.events.topic.name}",
        "${payments.events.topic.name}"
})
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsCommandTopicName;
    private final String paymentsCommandsTopicName;
    private final String ordersCommandsTopicName;
    private final OrderHistoryService orderHistoryService;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     @Value("${products.commands.topic.name}") String productsCommandTopicName,
                     @Value("${payments.commands.topic.name}") String paymentsCommandsTopicName,
                     @Value("${orders.commands.topic.name}") String ordersCommandsTopicName,
                     OrderHistoryService orderHistoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandTopicName = productsCommandTopicName;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
        this.ordersCommandsTopicName = ordersCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event){
        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                event.getProductId(),
                event.getProductQuantity(),
                event.getOrderId()
        );

        kafkaTemplate.send(productsCommandTopicName, reserveProductCommand);
        orderHistoryService.add(event.getOrderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductsReservedEvent event){

        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(
                event.getOrderId(),
                event.getProductId(),
                event.getProductPrice(),
                event.getProductQuantity()
        );

        kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event){
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(event.getOrderId());
        kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event){
        orderHistoryService.add(event.getOrderId(), OrderStatus.APPROVED);
    }
}
