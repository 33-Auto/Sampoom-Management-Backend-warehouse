package com.sampoom.backend.api.order.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.order.dto.OrderReqDto;
import com.sampoom.backend.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesEventConsumer {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "sales-event")
    public void consumer(String message) {
        try {
            OrderReqDto orderReqDto = objectMapper.readValue(message, OrderReqDto.class);

            orderService.orderProcess(orderReqDto);
            log.info("✅ Received OrderCreatedEvent: orderId={}, items={}", orderReqDto.getOrderId(), orderReqDto.getItems().size());
        } catch (Exception e) {
            log.error("❌ Failed to process PartGroupCreated event", e);
        }
    }
}
