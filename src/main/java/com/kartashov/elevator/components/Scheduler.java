package com.kartashov.elevator.components;

import com.kartashov.elevator.entities.Elevator;
import com.kartashov.elevator.entities.Job;
import com.kartashov.elevator.repositories.ElevatorRepository;
import com.kartashov.elevator.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scheduler is used to distribute incoming jobs among registered elevators in an optimal manner.
 *
 * The responsibility of a scheduler is to establish the connection to the elevators,
 * manage the jobs queue for every elevator, by picking the best elevator and bungling
 * as many individual requests into elevator trips as possible.
 *
 * There are many considerations that might influence the final design of the implementation
 * <ul>
 * <li>One may decide to distribute people among elevators as evenly as possible instead of trying
 *     to squeeze the max capacity into every lift
 * <li>One may decide to give late arriving people priority if their request aligns with already scheduled jobs
 * <li>One may decide to interrupt existing jobs to let additional people in
 * </ul>
 *
 * The general solution might be quite elaborate and needs to be driven by real statistical data,
 * and should probably incorporate more data, like bearding and exiting times,
 * probability of elevator failure and so on.
 */
@Component
@Transactional
public class Scheduler {

    private final int elevatorsCount;
    private final int maxCapacity;
    private final JobRepository jobRepository;
    private final ElevatorRepository elevatorRepository;

    /**
     * Create a scheduler.
     *
     * The responsibility of the scheduler are to manage elevators and job queues.
     * On start time we check that the database contains records for every elevator.
     */
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
        for (int i = 0; i < elevatorsCount; i++) {
            char id = (char) ('A' + i);
            Elevator elevator = elevatorRepository.findOne(id);
            if (elevator == null) {
                elevator = new Elevator(id);
                elevatorRepository.save(elevator);
            }
        }
        this.elevatorsCount = elevatorsCount;
        this.maxCapacity = maxCapacity;

    }

    /**
     * Call a batch request.
     */
    public void call(BatchRequest batchRequest) {
        for (Request request : batchRequest) {
            call(request);
        }
    }

    /**
     * Process job queues of each elevator.
     *
     * If the top job in the queue is not active we activate it.
     * If the elevator says it's done updating current job, we remove it from the queue.
     * It also the responsibility of the scheduler to persist all the changes to elevator's and jobs' statuses.
     */
    @Scheduled(fixedRate = 1000)
    public void process() {
        for (Elevator elevator : elevatorRepository.findAll()) {
            if (elevator.hasJobs()) {
                Job job = elevator.getTopJob();
                if (job.getState() != Job.State.ACTIVE) {
                    job.setState(Job.State.ACTIVE);
                    jobRepository.save(job);
                }
                if (elevator.update()) {
                    jobRepository.delete(job);
                    elevator.getJobs().remove(job);
                }
                elevatorRepository.save(elevator);
            }
        }
    }

    /**
     * Find the best queue for a new job request.
     */
    private void call(Request request) {
        List<Placement> placements = new ArrayList<>(elevatorsCount);
        for (Elevator elevator : elevatorRepository.findAll()) {
            placements.add(new Placement(elevator, request, maxCapacity));
        }
        Collections.sort(placements);
        Job job = placements.get(0).getJob();
        job.addPassenger();
        jobRepository.save(job);
    }
}
