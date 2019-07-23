package com.ui.reddittrends.consumer.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Event {
    private EventType type;
    private String author;
    private String subreddit;
    @JsonProperty("created_utc")
    private long created;
    private String title;
    @JsonProperty("body_html")
    private String body;
    @JsonProperty("subreddit_subscribers")
    private long subscribers;
}
