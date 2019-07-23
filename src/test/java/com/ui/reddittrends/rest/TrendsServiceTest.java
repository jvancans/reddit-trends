package com.ui.reddittrends.rest;

import com.ui.reddittrends.consumer.EventRepository;
import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.consumer.data.EventType;
import com.ui.reddittrends.rest.data.AuthorActivity;
import com.ui.reddittrends.rest.data.GeneralActivity;
import com.ui.reddittrends.rest.data.SubredditActivity;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.ui.reddittrends.TestUtils.stubEvent;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrendsServiceTest {
    @Mock
    private EventRepository repository;

    @InjectMocks
    private TrendsService service;

    @Test
    void testEventRetrieval() {
        Collection<Event> testEvents = stubEvents();
        when(repository.getEvents(argThat(Objects::isNull))).thenReturn(testEvents);

        Collection<Event> events = service.getEvents(null, 0L);

        assertThat(events, equalTo(testEvents));
    }

    @Test
    void testEventRetrievalWhenNoEventsMatch() {
        when(repository.getEvents(argThat(Objects::nonNull))).thenReturn(emptyList());

        Collection<Event> events = service.getEvents(ChronoUnit.DAYS, 1);

        assertThat(events, Matchers.empty());
    }

    @Test
    void testActivityRetrieval() {
        long commentCount = 2L;
        long submissionCount = 4L;
        when(repository.getCommentCount(argThat(Objects::isNull))).thenReturn(commentCount);
        when(repository.getSubmissionCount(argThat(Objects::isNull))).thenReturn(submissionCount);

        GeneralActivity activity = service.getActivity(null, 0L);

        assertThat(activity, allOf(
            hasProperty("commentCount", equalTo(commentCount)),
            hasProperty("submissionCount", equalTo(submissionCount))));
    }

    @Test
    void testActivityRetrievalWhenNoEventsMatch() {
        long noEventCount = 0L;
        when(repository.getCommentCount(argThat(Objects::nonNull))).thenReturn(noEventCount);
        when(repository.getSubmissionCount(argThat(Objects::nonNull))).thenReturn(noEventCount);

        GeneralActivity activity = service.getActivity(ChronoUnit.DAYS, 1);

        assertThat(activity, allOf(
            hasProperty("commentCount", equalTo(noEventCount)),
            hasProperty("submissionCount", equalTo(noEventCount))));
    }

    @Test
    void testTopSubreddits() {
        String topSubredditName = "topSubreddit";
        int topSubredditActivity = 10;
        String secondSubredditName = "secondSubreddit";
        int secondSubredditActivity = 5;
        String thirdSubredditName = "thirdSubreddit";
        int thirdSubredditActivity = 2;
        when(repository.getTopSubreddits(anyInt())).thenReturn(Stream.of(
            stubSubredditEntry(topSubredditName, topSubredditActivity),
            stubSubredditEntry(secondSubredditName, secondSubredditActivity),
            stubSubredditEntry(thirdSubredditName, thirdSubredditActivity)));

        Collection<SubredditActivity> topSubreddits = service.getTopSubreddits(Integer.MAX_VALUE);

        assertThat(topSubreddits, containsInRelativeOrder(
            allOf(hasProperty("subreddit", equalTo(topSubredditName)), hasProperty("activity", equalTo((long) topSubredditActivity))),
            allOf(hasProperty("subreddit", equalTo(secondSubredditName)), hasProperty("activity", equalTo((long) secondSubredditActivity))),
            allOf(hasProperty("subreddit", equalTo(thirdSubredditName)), hasProperty("activity", equalTo((long) thirdSubredditActivity)))));
    }

    @Test
    void testTopAuthors() {
        String topAuthorName = "topAuthor";
        int topAuthorActivity = 7;
        String secondAuthorName = "secondAuthor";
        int secondAuthorActivity = 4;
        String thirdAuthorName = "thirdAuthor";
        int thirdAuthorActivity = 1;
        when(repository.getTopAuthors(anyInt())).thenReturn(Stream.of(
            stubAuthorEventsInSubredditEntry(topAuthorName, topAuthorActivity),
            stubAuthorEventsInSubredditEntry(secondAuthorName, secondAuthorActivity),
            stubAuthorEventsInSubredditEntry(thirdAuthorName, thirdAuthorActivity)));

        Collection<AuthorActivity> topSubreddits = service.getTopAuthors(Integer.MAX_VALUE);

        assertThat(topSubreddits, containsInRelativeOrder(
            allOf(hasProperty("author", equalTo(topAuthorName)), hasProperty("activity", equalTo((long) topAuthorActivity))),
            allOf(hasProperty("author", equalTo(secondAuthorName)), hasProperty("activity", equalTo((long) secondAuthorActivity))),
            allOf(hasProperty("author", equalTo(thirdAuthorName)), hasProperty("activity", equalTo((long) thirdAuthorActivity)))));
    }

    private Map.Entry<String, Map<String, Collection<Event>>> stubSubredditEntry(String subreddit, int eventCount) {
        Collection<Event> userEventsInSubreddit = stubUserEvents(eventCount);
        Map<String, Collection<Event>> subredditEvents = Map.of("someUser", userEventsInSubreddit);
        return new AbstractMap.SimpleEntry<>(subreddit, subredditEvents);
    }

    private Map.Entry<String, List<Event>> stubAuthorEventsInSubredditEntry(String author, int eventCount) {
        List<Event> userEventsInSubreddit = stubUserEvents(eventCount);
        return new AbstractMap.SimpleEntry<>(author, userEventsInSubreddit);
    }

    private List<Event> stubUserEvents(int eventCount) {
        return IntStream.range(0, eventCount).mapToObj(i -> stubEvent(EventType.RS)).collect(toList());
    }

    private Collection<Event> stubEvents() {
        Event firstEvent = stubEvent(EventType.RC);
        Event secondEvent = stubEvent(EventType.RS);
        return List.of(firstEvent, secondEvent);
    }
}
