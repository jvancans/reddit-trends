package com.ui.reddittrends.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static java.util.Collections.singletonList;

@TestConfiguration
public class TestConfig {
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(singletonList(MediaType.APPLICATION_JSON));

        return new RestTemplateBuilder()
            .additionalMessageConverters(mappingJackson2HttpMessageConverter);
    }
}
