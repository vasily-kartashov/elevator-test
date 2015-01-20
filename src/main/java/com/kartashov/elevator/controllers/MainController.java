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
    private final int maxRequest;
    private final int numberOfLevels;

    @Autowired
    public MainController(Feed feed,
                          @Value("${elevator.max-request}") int maxRequest,
                          @Value("${elevator.levels}") int numberOfLevels) {
        this.feed = feed;
        this.maxRequest = maxRequest;
        this.numberOfLevels = numberOfLevels;
    }

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("numberOfLevels", numberOfLevels);
        model.put("elevators", feed.getElevators());
        model.put("maxRequest", maxRequest);
        model.put("feedData", feed.getFeedData());
        return "index";
    }
}
