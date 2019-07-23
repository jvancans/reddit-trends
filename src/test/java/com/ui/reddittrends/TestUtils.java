package com.ui.reddittrends;

import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.consumer.data.EventType;

public class TestUtils {
    public static final String IRRELEVANT_SUBREDDIT_NAME = "doesNotMatter";
    public static final String IRRELEVANT_AUTHOR_NAME = "someAuthor";
    public static final int THIRTY_SECONDS_IN_MILLIS = 1000 * 30;
    private static final int MINUTE_IN_MILLIS = 1000 * 60;

    private TestUtils() {
        throw new IllegalStateException("Trying to init utils class");
    }

    public static Event stubEvent(EventType type) {
        return stubEvent(type, IRRELEVANT_AUTHOR_NAME, IRRELEVANT_SUBREDDIT_NAME);
    }

    public static Event stubEvent(EventType type, String author, String subreddit) {
        Event event = new Event();
        event.setType(type);
        event.setSubreddit(subreddit);
        event.setCreated(System.currentTimeMillis() - MINUTE_IN_MILLIS);
        event.setAuthor(author);
        return event;
    }
}
