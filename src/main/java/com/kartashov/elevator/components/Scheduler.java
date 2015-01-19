package com.kartashov.elevator.components;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kartashov.elevator.entities.Elevator;
import com.kartashov.elevator.entities.Job;
import com.kartashov.elevator.repositories.ElevatorRepository;
import com.kartashov.elevator.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
@Transactional
public class Scheduler {

    private final int elevatorsCount;
    private final int maxCapacity;
    private final JobRepository jobRepository;
    private final ElevatorRepository elevatorRepository;
    private final Map<Character, Elevator.Position> elevatorPositions;

    @Autowired
    public Scheduler(JobRepository jobRepository,
                     ElevatorRepository elevatorRepository,
                     @Value("${elevator.count}") int elevatorsCount,
                     @Value("${elevator.capacity}") int maxCapacity) {

        if (elevatorsCount > 30) {
            throw new IllegalArgumentException("Cannot have more than 30 elevators.");
        }
        this.jobRepository = jobRepository;
        this.elevatorRepository = elevatorRepository;
        this.elevatorPositions = new LinkedHashMap<>();
        for (int i = 0; i < elevatorsCount; i++) {
            char id = (char) ('A' + i);
            Elevator elevator = elevatorRepository.findOne(id);
            if (elevator == null) {
                elevator = new Elevator(id);
                elevatorRepository.save(elevator);
                elevatorPositions.put(id, elevator.getPosition());
            }
        }
        this.elevatorsCount = elevatorsCount;
        this.maxCapacity = maxCapacity;

    }

    public void call(BatchRequest batchRequest) {
        for (Request request : batchRequest) {
            call(request);
        }
    }

    @JsonValue
    public Map<Character, Elevator.Position> getElevatorPositions() {
        return elevatorPositions;
    }

    @Scheduled(fixedRate = 1000)
    public void process() {
        for (Elevator elevator : elevatorRepository.findAll()) {
            Job job = jobRepository.findTopByElevator(elevator);
            if (job != null) {
                if (job.getState() != Job.State.ACTIVE) {
                    job.setState(Job.State.ACTIVE);
                    jobRepository.save(job);
                }
                if (elevator.update(job)) {
                    jobRepository.delete(job);
                }
                elevatorRepository.save(elevator);
                elevatorPositions.put(elevator.getId(), elevator.getPosition(job));
            }
        }
    }

    private void call(Request request) {
        List<Placement> placements = new ArrayList<>(elevatorsCount);
        Iterable<Job> jobs = jobRepository.findAll();
        for (Elevator elevator : elevatorRepository.findAll()) {
            placements.add(new Placement(request, jobs, elevator, maxCapacity));
        }
        Collections.sort(placements);
        Job job = placements.get(0).getJob();
        job.addPassenger();
        jobRepository.save(job);
    }

    private static class Placement implements Comparable<Placement> {

        private Job job;
        private int score;

        private Placement(Request request, Iterable<Job> jobs, Elevator elevator, int maxCapacity) {
            for (Job job : jobs) {
                if (job.getElevator().equals(elevator)) {
                    if (job.getState() == Job.State.ACTIVE) {
                        if (elevator.getState() == Elevator.State.CALLING) {
                            score += Math.abs(elevator.getLevel() - job.getFrom());
                        } else if (elevator.getState() == Elevator.State.CARRYING) {
                            score += Math.abs(elevator.getLevel() - job.getTo());
                        }
                    } else if (accepts(job, request, maxCapacity)) {
                        this.job = job;
                    } else {
                        score += Math.abs(job.getTo() - job.getFrom());
                    }
                }
            }
            if (job == null) {
                job = new Job(elevator, request);
                score = Math.abs(request.getFrom() - elevator.getLevel());
            }
        }

        public Job getJob() {
            return job;
        }

        @Override
        public int compareTo(Placement o) {
            return Integer.compare(score, o.score);
        }

        private boolean accepts(Job job, Request request, int maxCapacity) {
            return job.getFrom() == request.getFrom()
                    && job.getTo() == request.getTo()
                    && job.getPassengers() < maxCapacity;
        }

        @Override
        public String toString() {
            return String.format("Score is %d", score);
        }
    }
}
