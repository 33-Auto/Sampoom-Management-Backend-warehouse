package com.sampoom.backend.api.part.event;

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
        // 계속 추가
    }

    public Class<?> getPayloadClass(String eventType) {
        return registry.get(eventType);
    }
}
