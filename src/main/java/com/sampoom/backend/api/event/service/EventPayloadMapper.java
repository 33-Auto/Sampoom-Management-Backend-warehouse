package com.sampoom.backend.api.event.service;

import com.sampoom.backend.api.bom.dto.BomPayload;
import com.sampoom.backend.api.branch.dto.BranchPayload;
import com.sampoom.backend.api.branch.dto.DistancePayload;
import com.sampoom.backend.api.inventory.dto.ForecastPayload;
import com.sampoom.backend.api.order.dto.POEventPayload;
import com.sampoom.backend.api.part.dto.PartCategoryPayload;
import com.sampoom.backend.api.part.dto.PartGroupPayload;
import com.sampoom.backend.api.part.dto.PartPayload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventPayloadMapper {

    private final Map<String, Class<?>> registry = new HashMap<>();

    public EventPayloadMapper() {
        registry.put("PartCategoryCreated", PartCategoryPayload.class);
        registry.put("PartGroupCreated", PartGroupPayload.class);
        registry.put("PartCreated", PartPayload.class);
        registry.put("PartUpdated", PartPayload.class);

        registry.put("BranchCreated", BranchPayload.class);
        registry.put("DistanceCalculated", DistancePayload.class);

        registry.put("PartOrderCreated", POEventPayload.class);
        registry.put("PartOrderStatusChanged", POEventPayload.class);
        registry.put("PartOrderCompleted", POEventPayload.class);

        registry.put("PartForecast", ForecastPayload.class);

        registry.put("BomCreated", BomPayload.class);
        registry.put("BomUpdated", BomPayload.class);
    }

    public Class<?> getPayloadClass(String eventType) {
        return registry.get(eventType);
    }
}
