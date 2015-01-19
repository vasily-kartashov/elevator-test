package com.kartashov.elevator.controllers;

import com.kartashov.elevator.components.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class MainController {

    private final Scheduler scheduler;
    private final int numberOfLevels;

    @Autowired
    public MainController(Scheduler scheduler, @Value("${elevator.levels}") int numberOfLevels) {
        this.scheduler = scheduler;
        this.numberOfLevels = numberOfLevels;
    }

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("numberOfLevels", numberOfLevels);
        return "index";
    }
}
