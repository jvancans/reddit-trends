package com.ui.reddittrends.consumer.data;

import static java.util.Arrays.stream;

public enum EventType {
    RS, RC;

    public static EventType resolve(String eventType) {
        return stream(EventType.values())
            .filter(type -> type.name().equalsIgnoreCase(eventType))
            .findFirst()
            .orElse(null);
    }
}
