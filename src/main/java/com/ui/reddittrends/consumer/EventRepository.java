package com.ui.reddittrends.consumer;


import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.consumer.data.EventType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static reactor.util.CollectionUtils.isEmpty;

@Component
public class EventRepository {
    private ConsumerProperties properties;
    private Map<String, Map<String, Collection<Event>>> userEventsInSubreddits = new ConcurrentHashMap<>();

    @Inject
    public EventRepository(ConsumerProperties properties) {
        this.properties = properties;
    }

    void push(Event event) {
        String subreddit = event.getSubreddit();
        userEventsInSubreddits.compute(subreddit, addToSubredditEntries(event));
    }

    public Collection<Event> getEvents(Predicate<Event> eventPredicate) {
        Stream<Event> allEventStream = getAllEventStream();
        if (eventPredicate != null) {
            return allEventStream.filter(eventPredicate).collect(toList());
        }
        return allEventStream.collect(toList());
    }

    public long getSubmissionCount(Predicate<Event> eventPredicate) {
        return getEventCount(EventType.RS, eventPredicate);
    }

    public long getCommentCount(Predicate<Event> eventPredicate) {
        return getEventCount(EventType.RC, eventPredicate);
    }

    public Stream<Map.Entry<String, Map<String, Collection<Event>>>> getTopSubreddits(int limitResults) {
        return synchronizedEventEntries()
            .stream()
            .sorted(reverseOrder(compareSubredditsByActivity()))
            .limit(limitResults);
    }

    public Stream<Map.Entry<String, List<Event>>> getTopAuthors(int limitResults) {
        return synchronizedEventEntries()
            .stream()
            .flatMap(eventsInSubreddit -> eventsInSubreddit.getValue().entrySet().stream())
            .flatMap(authorEventsInSubreddit -> authorEventsInSubreddit.getValue().stream())
            .filter(groupedAuthorEvents -> isNotExcludedUsername(groupedAuthorEvents.getAuthor()))
            .collect(groupingBy(Event::getAuthor))
            .entrySet()
            .stream()
            .sorted(reverseOrder(compareUsersByActivity()))
            .limit(limitResults);
    }

    private boolean isNotExcludedUsername(String author) {
        return !properties.getNotActualUsernames().contains(author);
    }

    private BiFunction<String, Map<String, Collection<Event>>, Map<String, Collection<Event>>> addToSubredditEntries(Event event) {
        return (subreddit, authorEvents) -> {
            String author = event.getAuthor();
            if (firstEventInSubreddit(authorEvents)) {
                Collection<Event> events = synchronizedCollection(buildAuthorEventsCollection(event));
                return synchronizedMap(buildSubredditAuthorEventsMap(author, events));
            }
            authorEvents.compute(author, addToAuthorEntries(event));
            return authorEvents;
        };
    }

    private boolean firstEventInSubreddit(Map<String, Collection<Event>> authorEvents) {
        return isEmpty(authorEvents);
    }

    private Collection<Event> buildAuthorEventsCollection(Event event) {
        int expectedEventsPerAuthorInSubreddit = properties.getAverageAuthorActivityInSubreddit();
        Collection<Event> userEvents = new ArrayList<>(expectedEventsPerAuthorInSubreddit);
        userEvents.add(event);
        return userEvents;
    }

    private Map<String, Collection<Event>> buildSubredditAuthorEventsMap(String author, Collection<Event> events) {
        int expectedUniqueUserEventsInSubreddit = properties.getAverageUniqueUserActivityInSubreddit();
        Map<String, Collection<Event>> subredditAuthorEvents = new HashMap<>(expectedUniqueUserEventsInSubreddit);
        subredditAuthorEvents.put(author, events);
        return subredditAuthorEvents;
    }

    private BiFunction<String, Collection<Event>, Collection<Event>> addToAuthorEntries(Event event) {
        return (author, events) -> {
            if (firstAuthorEventInSubreddit(events)) {
                return buildAuthorEventsCollection(event);
            }
            events.add(event);
            return events;
        };
    }

    private boolean firstAuthorEventInSubreddit(Collection<Event> events) {
        return isEmpty(events);
    }

    private long getEventCount(EventType type, Predicate<Event> eventPredicate) {
        Stream<Event> eventsByType = getAllEventStream()
            .filter(event -> type.equals(event.getType()));

        if (eventPredicate == null) {
            return eventsByType.count();
        }
        return eventsByType
            .filter(eventPredicate)
            .count();
    }

    private Stream<Event> getAllEventStream() {
        return synchronizedEventEntries()
            .stream()
            .flatMap(eventsInSubreddit -> eventsInSubreddit.getValue().entrySet()
                .stream()
                .flatMap(authorEventsInSubreddit -> authorEventsInSubreddit.getValue().stream()));
    }

    private Set<Map.Entry<String, Map<String, Collection<Event>>>> synchronizedEventEntries() {
        return synchronizedSet(userEventsInSubreddits.entrySet());
    }

    private Comparator<Map.Entry<String, Map<String, Collection<Event>>>> compareSubredditsByActivity() {
        return (firstSubreddit, secondSubreddit) -> {
            int activityInFirstSubreddit = getSubredditActivity(firstSubreddit);
            int activityInSecondSubreddit = getSubredditActivity(secondSubreddit);
            return Integer.compare(activityInFirstSubreddit, activityInSecondSubreddit);
        };
    }

    private int getSubredditActivity(Map.Entry<String, Map<String, Collection<Event>>> eventsInSubreddit) {
        return eventsInSubreddit.getValue().values().stream().mapToInt(Collection::size).sum();
    }

    private Comparator<Map.Entry<String, List<Event>>> compareUsersByActivity() {
        return (firstAuthorEvents, secondAuthorEvents) -> {
            int firstAuthorActivity = firstAuthorEvents.getValue().size();
            int secondAuthorActivity = secondAuthorEvents.getValue().size();
            return Integer.compare(firstAuthorActivity, secondAuthorActivity);
        };
    }
}
