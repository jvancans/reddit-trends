package com.ui.reddittrends.rest.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubredditActivity {
    private String subreddit;

    private long activity;
}
