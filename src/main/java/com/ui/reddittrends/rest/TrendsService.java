package com.ui.reddittrends.rest;

import com.ui.reddittrends.consumer.EventRepository;
import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.rest.data.AuthorActivity;
import com.ui.reddittrends.rest.data.GeneralActivity;
import com.ui.reddittrends.rest.data.SubredditActivity;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toUnmodifiableList;

@Component
class TrendsService {
    private static final ZoneId UTC_ZONE_ID = ZoneId.of(ZoneOffset.UTC.getId());
    private EventRepository repository;

    @Inject
    TrendsService(EventRepository repository) {
        this.repository = repository;
    }

    Collection<Event> getEvents(ChronoUnit timeUnit, long timeValue) {
        return repository.getEvents(getEventCreatedAfterPredicate(timeUnit, timeValue));
    }

    GeneralActivity getActivity(ChronoUnit timeUnit, long timeValue) {
        long submissionCount = repository.getSubmissionCount(getEventCreatedAfterPredicate(timeUnit, timeValue));
        long commentCount = repository.getCommentCount(getEventCreatedAfterPredicate(timeUnit, timeValue));
        return new GeneralActivity(submissionCount, commentCount);
    }

    Collection<SubredditActivity> getTopSubreddits(int limitResults) {
        return repository.getTopSubreddits(limitResults)
            .map(this::mapToSubredditActivity)
            .collect(toUnmodifiableList());
    }

    List<AuthorActivity> getTopAuthors(int limitResults) {
        return repository.getTopAuthors(limitResults)
            .map(this::mapToAuthorActivity)
            .collect(toUnmodifiableList());
    }

    private Predicate<Event> getEventCreatedAfterPredicate(ChronoUnit timeUnit, long timeValue) {
        if (timeUnit == null || timeValue == 0L) {
            return null;
        }
        return eventCreationTimePredicate(timeUnit, timeValue);
    }

    private Predicate<Event> eventCreationTimePredicate(ChronoUnit timeUnit, long timeValue) {
        return event -> {
            LocalDateTime fromTime = LocalDateTime.now(UTC_ZONE_ID).minus(timeValue, timeUnit);
            LocalDateTime eventTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getCreated()), UTC_ZONE_ID);
            return eventTime.isEqual(fromTime) || eventTime.isAfter(fromTime);
        };
    }

    private SubredditActivity mapToSubredditActivity(Map.Entry<String, Map<String, Collection<Event>>> eventsInSubreddit) {
        String subreddit = eventsInSubreddit.getKey();
        int allAuthorEventsInSubreddit = eventsInSubreddit.getValue().values().stream().mapToInt(Collection::size).sum();
        return new SubredditActivity(subreddit, allAuthorEventsInSubreddit);
    }

    private AuthorActivity mapToAuthorActivity(Map.Entry<String, List<Event>> authorEventsInSubreddit) {
        String author = authorEventsInSubreddit.getKey();
        int activity = authorEventsInSubreddit.getValue().size();
        return new AuthorActivity(author, activity);
    }
}
