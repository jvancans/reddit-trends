package com.ui.reddittrends.config;

import com.ui.reddittrends.rest.TrendsResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(TrendsResource.class);
    }
}
