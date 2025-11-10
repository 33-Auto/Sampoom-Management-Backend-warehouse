//package com.sampoom.backend.api.order.event;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sampoom.backend.api.order.dto.POEventPayload;
//import com.sampoom.backend.api.order.service.OrderService;
//import com.sampoom.backend.api.order.service.PurchaseOrderService;
//import com.sampoom.backend.api.event.entity.Event;
//import com.sampoom.backend.api.event.service.EventPayloadMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class PurchaseEventConsumer {
//    private final ObjectMapper objectMapper;
//    private final PurchaseOrderService purchaseOrderService;
//    private final EventPayloadMapper eventPayloadMapper;
//
//    @KafkaListener(topics = "part-order-events")
//    public void consumer(String message) {
//        try {
//            JsonNode root = objectMapper.readTree(message);
//            String eventType = root.get("eventType").asText();
//            Class<?> payloadClass = eventPayloadMapper.getPayloadClass(eventType);
//
//            if (payloadClass == null) {
//                log.error("⚠️ Unknown eventType: {}", eventType);
//                return ;
//            }
//
//            Event<?> event = objectMapper.readValue(
//                    message,
//                    objectMapper.getTypeFactory().constructParametricType(Event.class, payloadClass)
//            );
//            POEventPayload payload = (POEventPayload) event.getPayload();
//
//            if ("PartOrderStatusChanged".equals(eventType)) {
//                purchaseOrderService.updatePOStatus(payload);
//                log.info("✅ PartOrderStatusChanged saved: {}", payload.getPartOrderId());
//            }
//            else if ("PartOrderCompleted".equals(eventType)) {
//                purchaseOrderService.completePOStatus(payload);
//                log.info("✅ PartOrderCompleted saved: {}", payload.getPartOrderId());
//            }
//        } catch (Exception e) {
//            log.error("❌ Failed to process purchase order event: {}, {}", message, e.getMessage());
//            throw new RuntimeException("Kafka message processing failed", e);
//        }
//    }
//}