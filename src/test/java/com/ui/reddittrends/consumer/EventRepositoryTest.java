package com.ui.reddittrends.consumer;

import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.consumer.data.EventType;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.ui.reddittrends.TestUtils.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryTest {
    private static final Predicate<Event> EVENT_CREATED_IN_PAST_THIRTY_SECONDS = event -> event.getCreated() >= System.currentTimeMillis() - THIRTY_SECONDS_IN_MILLIS;

    @Mock
    private ConsumerProperties properties;

    @InjectMocks
    private EventRepository repository;

    @Test
    void testEventRetrieval() {
        Event testEvent = stubEvent(EventType.RS);
        repository.push(testEvent);

        Collection<Event> events = repository.getEvents(null);

        assertThat(events, hasItem(equalTo(testEvent)));
    }

    @Test
    void testRecentEventRetrieval() {
        Event testEvent = stubEvent(EventType.RS, IRRELEVANT_AUTHOR_NAME, IRRELEVANT_SUBREDDIT_NAME);
        repository.push(testEvent);

        Collection<Event> eventsInPastThirtySeconds = repository.getEvents(EVENT_CREATED_IN_PAST_THIRTY_SECONDS);
        assertThat(eventsInPastThirtySeconds, empty());
    }

    @Test
    void testTopAuthorsRetrieval() {
        String topAuthor = "1st";
        Event firstEvent = stubEvent(EventType.RS, topAuthor, IRRELEVANT_SUBREDDIT_NAME);
        Event secondEvent = stubEvent(EventType.RS, topAuthor, IRRELEVANT_SUBREDDIT_NAME);

        Event thirdEvent = stubEvent(EventType.RS);

        repository.push(firstEvent);
        repository.push(thirdEvent);
        repository.push(secondEvent);

        List<Map.Entry<String, List<Event>>> topAuthorEntries = repository.getTopAuthors(Integer.MAX_VALUE)
            .collect(toList());

        assertThat(topAuthorEntries, containsInRelativeOrder(
            entryForKey(topAuthor),
            entryForKey(IRRELEVANT_AUTHOR_NAME)));
    }

    @Test
    void testActivity() {
        repository.push(stubEvent(EventType.RS));
        repository.push(stubEvent(EventType.RC));
        repository.push(stubEvent(EventType.RS));

        long commentCount = repository.getCommentCount(null);
        long submissionCount = repository.getSubmissionCount(null);

        assertThat(commentCount, equalTo(1L));
        assertThat(submissionCount, equalTo(2L));
    }

    @Test
    void testRecentActivity() {
        repository.push(stubEvent(EventType.RS));
        repository.push(stubEvent(EventType.RC));

        long recentCommentCount = repository.getCommentCount(EVENT_CREATED_IN_PAST_THIRTY_SECONDS);
        long recentSubmissionCount = repository.getSubmissionCount(EVENT_CREATED_IN_PAST_THIRTY_SECONDS);

        assertThat(recentCommentCount, equalTo(0L));
        assertThat(recentSubmissionCount, equalTo(0L));
    }

    @Test
    void testTopSubreddits() {
        String topSubreddit = "1st";
        String secondSubreddit = "2nd";
        String thirdSubreddit = "3rd";
        repository.push(stubEvent(EventType.RC, IRRELEVANT_AUTHOR_NAME, topSubreddit));
        repository.push(stubEvent(EventType.RS, IRRELEVANT_AUTHOR_NAME, secondSubreddit));
        repository.push(stubEvent(EventType.RS, IRRELEVANT_AUTHOR_NAME, topSubreddit));
        repository.push(stubEvent(EventType.RC, IRRELEVANT_AUTHOR_NAME, topSubreddit));
        repository.push(stubEvent(EventType.RS, IRRELEVANT_AUTHOR_NAME, secondSubreddit));
        repository.push(stubEvent(EventType.RC, IRRELEVANT_AUTHOR_NAME, thirdSubreddit));

        List<Map.Entry<String, Map<String, Collection<Event>>>> topSubredditEntries = repository.getTopSubreddits(Integer.MAX_VALUE).collect(toList());

        assertThat(topSubredditEntries, containsInRelativeOrder(
            entryForKey(topSubreddit),
            entryForKey(secondSubreddit),
            entryForKey(thirdSubreddit)));
    }

    private <T> Matcher<Map.Entry<String, T>> entryForKey(String key) {
        return hasProperty("key", equalTo(key));
    }
}
