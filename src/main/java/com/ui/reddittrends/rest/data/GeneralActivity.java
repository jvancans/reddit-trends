package com.ui.reddittrends.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralActivity {
    @JsonProperty("submission_count")
    private long submissionCount;

    @JsonProperty("comment_count")
    private long commentCount;
}
