package com.kartashov.elevator.controllers;

import com.kartashov.elevator.components.BatchRequest;
import com.kartashov.elevator.components.Feed;
import com.kartashov.elevator.components.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ServiceController {

    private final Scheduler scheduler;
    private final Feed feed;
    private final int numberOfLevels;
    private final int maxRequest;

    @Autowired
    public ServiceController(Scheduler scheduler, Feed feed,
                             @Value("${elevator.levels}") int numberOfLevels,
                             @Value("${elevator.max-request}") int maxRequest) {
        this.scheduler = scheduler;
        this.feed = feed;
        this.numberOfLevels = numberOfLevels;
        this.maxRequest = maxRequest;
    }

    @RequestMapping("/service/feed")
    public Feed.FeedData getFeed() {
        return feed.getFeedData();
    }

    @RequestMapping(value = "/service/call", method = RequestMethod.POST)
    public void call(@RequestBody BatchRequest request) {
        if (request.getFrom() >= numberOfLevels
                || request.getTo() >= numberOfLevels
                || request.getPassengers() > maxRequest) {
            throw new RuntimeException("Invalid request");
        }
        scheduler.call(request);
    }
}
