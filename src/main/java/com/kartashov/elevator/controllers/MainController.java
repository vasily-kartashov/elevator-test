package com.kartashov.elevator.controllers;

import com.kartashov.elevator.components.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class MainController {

    private final Feed feed;
    private final int numberOfLevels;
    private final int maxRequest;

    @Autowired
    public MainController(Feed feed,
                          @Value("${elevator.levels}") int numberOfLevels,
                          @Value("${elevator.max-request}") int maxRequest) {
        this.feed = feed;
        this.numberOfLevels = numberOfLevels;
        this.maxRequest = maxRequest;
    }

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("numberOfLevels", numberOfLevels);
        model.put("maxRequest", maxRequest);
        model.put("feedData", feed.getFeedData());
        return "index";
    }
}
