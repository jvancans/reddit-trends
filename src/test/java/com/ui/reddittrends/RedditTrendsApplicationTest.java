package com.ui.reddittrends;

import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.rest.data.AuthorActivity;
import com.ui.reddittrends.rest.data.GeneralActivity;
import com.ui.reddittrends.rest.data.SubredditActivity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.util.StringUtils;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RedditTrendsApplicationTest extends AbstractIntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "timeUnit=MINUTES&timeValue=1",
        "timeUnit=MINUTES&timeValue=5",
        "timeUnit=HOURS&timeValue=1",
        "timeUnit=DAYS&timeValue=1",
        ""})
    void testPastEvents(String params) {
        String uri = getBaseUrl() + "/api/trends" + appendOptionalParams(params);
        ParameterizedTypeReference<Collection<Event>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Collection<Event>> response = restTemplate.exchange(uri, HttpMethod.GET, null, responseType, params);
        assertResponseSuccess(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "timeUnit=MINUTES&timeValue=1",
        "timeUnit=MINUTES&timeValue=5",
        "timeUnit=HOURS&timeValue=1",
        "timeUnit=DAYS&timeValue=1",
        ""})
    void testPastActivity(String params) {
        String uri = getBaseUrl() + "/api/trends/activity" + appendOptionalParams(params);
        ParameterizedTypeReference<GeneralActivity> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<GeneralActivity> response = restTemplate.exchange(uri, HttpMethod.GET, null, responseType, params);
        assertResponseSuccess(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "limit=100",
        ""})
    void testTopSubreddits(String params) {
        String uri = getBaseUrl() + "/api/trends/subreddits" + appendOptionalParams(params);
        ParameterizedTypeReference<Collection<SubredditActivity>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Collection<SubredditActivity>> response = restTemplate.exchange(uri, HttpMethod.GET, null, responseType, params);
        assertResponseSuccess(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "limit=10",
        ""})
    void testTopUsers(String params) {
        String uri = getBaseUrl() + "/api/trends/users" + appendOptionalParams(params);
        ParameterizedTypeReference<Collection<AuthorActivity>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Collection<AuthorActivity>> response = restTemplate.exchange(uri, HttpMethod.GET, null, responseType, params);
        assertResponseSuccess(response);
    }

    private String appendOptionalParams(String params) {
        return StringUtils.isEmpty(params) ? "" : "?" + params;
    }

    private void assertResponseSuccess(ResponseEntity<?> eventResponse) {
        assertEquals(HttpStatus.OK, eventResponse.getStatusCode());
        assertNotNull(eventResponse);
        assertNotNull(eventResponse.getBody());
    }
}
