package com.ui.reddittrends.consumer;

import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.consumer.data.EventType;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static com.ui.reddittrends.consumer.data.EventType.resolve;

@Log4j2
@Component
class ConsumerService {
    private static final String KEEP_ALIVE_EVENT_NAME = "keepalive";
    private ConsumerProperties properties;
    private EventRepository repository;

    @Inject
    ConsumerService(ConsumerProperties properties, EventRepository repository) {
        this.properties = properties;
        this.repository = repository;
    }

    private static boolean notKeepAliveEvent(String eventName) {
        return !KEEP_ALIVE_EVENT_NAME.equals(eventName);
    }

    @PostConstruct
    void consumeEvents() {
        WebClient client = WebClient.create(properties.getProducerUrl());

        Flux<ServerSentEvent<Event>> eventStream = client.get()
            .retrieve()
            .bodyToFlux(new EventTypeReference());

        eventStream.subscribe(this::processContent, this::processError);
    }

    private void processContent(ServerSentEvent<Event> content) {
        if (content == null) {
            log.error("Process request with no content");
        } else if (validEvent(content)) {
            Event event = content.data();
            log.debug("Pushing event to repository: {}", event);
            repository.push(event);
        } else if (notKeepAliveEvent(content.event())) {
            log.warn("Invalid event: \nid: {}\nevent: {}\ndata: {}", content.id(), content.event(), content.data());
        }
    }

    private boolean validEvent(ServerSentEvent<Event> content) {
        Event event = content.data();
        if (event == null) {
            return false;
        }
        EventType resolvedType = resolve(content.event());
        if (resolvedType == null) {
            return false;
        }
        event.setType(resolvedType);
        return true;
    }

    private void processError(Throwable error) {
        log.error("Unexpected error", error);
    }

    private class EventTypeReference extends ParameterizedTypeReference<ServerSentEvent<Event>> {

    }
}
