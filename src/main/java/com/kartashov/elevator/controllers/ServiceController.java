package com.kartashov.elevator.controllers;

import com.kartashov.elevator.components.BatchRequest;
import com.kartashov.elevator.components.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {

    private final Scheduler scheduler;

    @Autowired
    public ServiceController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @RequestMapping("/service/state")
    public Scheduler getState() {
        return scheduler;
    }

    @RequestMapping(value = "/service/call", method = RequestMethod.POST)
    public void call(@RequestBody BatchRequest request) {
        scheduler.call(request);
    }
}
