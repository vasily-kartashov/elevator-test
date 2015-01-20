package com.kartashov.elevator.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kartashov.elevator.entities.Elevator;
import com.kartashov.elevator.entities.Job;
import com.kartashov.elevator.repositories.ElevatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Feed to the application state. The information contained is fetched from the database
 */
@Component
@Transactional
public class Feed {

    private final ElevatorRepository elevatorRepository;
    private final int numberOfLevels;

    @Autowired
    public Feed(ElevatorRepository elevatorRepository, @Value("${elevator.levels}") int numberOfLevels) {
        this.elevatorRepository = elevatorRepository;
        this.numberOfLevels = numberOfLevels;
    }

    public FeedData getFeedData() {
        int[] waiting = new int[numberOfLevels];
        Map<Character, ElevatorPosition> elevators = new LinkedHashMap<>();
        for (Elevator elevator : elevatorRepository.findAll()) {
            elevators.put(elevator.getId(), new ElevatorPosition(elevator));
            for (Job job : elevator.getJobs()) {
                if (job.getState() != Job.State.ACTIVE || elevator.getState() != Elevator.State.CARRYING) {
                    waiting[job.getFrom()] += job.getPassengers();
                }
            }
        }
        return new FeedData(elevators, waiting);
    }

    public List<Character> getElevators() {
        List<Character> elevators = new ArrayList<>();
        for (Elevator elevator : elevatorRepository.findAll()) {
            elevators.add(elevator.getId());
        }
        Collections.sort(elevators);
        return elevators;
    }

    public static class FeedData {

        private final Map<Character, ElevatorPosition> elevators;
        private final int[] waiting;

        public FeedData(Map<Character, ElevatorPosition> elevators, int[] waiting) {
            this.elevators = elevators;
            this.waiting = waiting;
        }

        @JsonProperty
        public Map<Character, ElevatorPosition> getElevators() {
            return elevators;
        }

        @JsonProperty
        public int[] getWaiting() {
            return waiting;
        }
    }

    public static class ElevatorPosition {

        private int level;
        private String direction;
        private int passengers;

        public ElevatorPosition(Elevator elevator) {
            direction = "steady";
            passengers = 0;
            level = elevator.getLevel();
            if (elevator.hasJobs()) {
                Job job = elevator.getTopJob();
                if (elevator.getState() == Elevator.State.CALLING) {
                    direction = level < job.getFrom() ? "up" : (level > job.getFrom() ? "down" : "steady");
                } else if (elevator.getState() == Elevator.State.CARRYING) {
                    direction = level < job.getTo() ? "up" : (level > job.getTo() ? "down" : "steady");
                    passengers = job.getPassengers();
                } else if (elevator.getState() == Elevator.State.EXITING) {
                    passengers = job.getPassengers();
                }
            }
        }

        @JsonProperty
        public int getLevel() {
            return level;
        }

        @JsonProperty
        public String getDirection() {
            return direction;
        }

        @JsonProperty
        public int getPassengers() {
            return passengers;
        }
    }
}
