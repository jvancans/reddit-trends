package com.ui.reddittrends.rest;

import com.ui.reddittrends.consumer.data.Event;
import com.ui.reddittrends.rest.data.AuthorActivity;
import com.ui.reddittrends.rest.data.GeneralActivity;
import com.ui.reddittrends.rest.data.SubredditActivity;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@Path("/trends")
@Produces("application/json")
public class TrendsResource {
    private TrendsService service;

    @Inject
    public TrendsResource(TrendsService service) {
        this.service = service;
    }

    @GET
    public Collection<Event> pastEvents(@QueryParam("timeUnit") ChronoUnit unit, @QueryParam("timeValue") long value) {
        return service.getEvents(unit, value);
    }

    @GET
    @Path("/activity")
    public GeneralActivity pastActivity(@QueryParam("timeUnit") ChronoUnit unit, @QueryParam("timeValue") long value) {
        return service.getActivity(unit, value);
    }

    @GET
    @Path("/subreddits")
    public Collection<SubredditActivity> topSubreddits(@QueryParam(value = "limit") @DefaultValue("100") int limitResults) {
        return service.getTopSubreddits(limitResults);
    }

    @GET
    @Path("/users")
    public Collection<AuthorActivity> topUsers(@QueryParam(value = "limit") @DefaultValue("10") int limitResults) {
        return service.getTopAuthors(limitResults);
    }
}
