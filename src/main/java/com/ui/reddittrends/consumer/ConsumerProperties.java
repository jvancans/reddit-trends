package com.ui.reddittrends.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "consumer")
public class ConsumerProperties {
    @Getter
    @Setter
    private String producerUrl;

    @Getter
    @Setter
    private int averageAuthorActivityInSubreddit;

    @Getter
    @Setter
    private int averageUniqueUserActivityInSubreddit;

    @Getter
    @Setter
    private List<String> notActualUsernames;
}
