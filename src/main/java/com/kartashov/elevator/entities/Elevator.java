package com.kartashov.elevator.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Elevator {

    private static final Logger logger = LoggerFactory.getLogger(Elevator.class);

    /**
     * The current state of the job.
     * <ul>
     * <li>{@code IDLE} the elevator is not moving and not occupied
     * <li>{@code CALLING} the elevator is moving towards the caller
     * <li>{@code BOARDING} the elevator is not moving while people are boarding it
     * <li>{@code CARRYING} the elevator is moving passengers to the destination
     * <li>{@code EXITING} the elevator has reached the destination and people are exiting it
     * </ul>
     */
    public enum State { IDLE, CALLING, BOARDING, CARRYING, State, EXITING }

    @Id
    private Character id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "elevator")
    private List<Job> jobs = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private State state;

    @Column
    private Integer level;

    public Elevator() {
        state = State.IDLE;
        level = 0;
    }

    public Elevator(Character id) {
        this();
        this.id = id;
    }

    public Character getId() {
        return id;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public boolean hasJobs() {
        return !jobs.isEmpty();
    }

    public Job getTopJob() {
        return jobs.get(0);
    }

    public State getState() {
        return state;
    }

    public Integer getLevel() {
        return level;
    }

    /**
     * Update the state of the elevator.
     *
     * @return flag if the current job is done and can be removed from the database
     * @throws java.lang.IllegalStateException if the top job in the queue is not active.
     *         There is very little an elevator can do about it, as this is usually
     *         an indication of some programming error.
     */
    public boolean update() {
        Job job = getTopJob();
        if (job.getState() != Job.State.ACTIVE) {
            throw new IllegalStateException("The top job is not active");
        }
        switch (state) {
            case IDLE:
                state = State.CALLING;
                // intentional fall through
            case CALLING:
                if (job.getFrom().equals(level)) {
                    logger.info("Elevator {}: {} passengers are entering on level {}", id, job.getPassengers(), level);
                    state = State.BOARDING;
                } else {
                    moveTowards(job.getFrom());
                }
                return false;
            case BOARDING:
                state = State.CARRYING;
                return false;
            case CARRYING:
                if (job.getTo().equals(level)) {
                    logger.info("Elevator {}: {} passengers are exiting on level {}", id, job.getPassengers(), level);
                    state = State.EXITING;
                } else {
                    moveTowards(job.getTo());
                }
                return false;
            case EXITING:
                state = State.IDLE;
                return true;
            default:
                throw new AssertionError("State is unaccounted for");
        }
    }

    private void moveTowards(int target) {
        if (level < target) {
            level++;
        } else {
            level--;
        }
    }

    @Override
    public String toString() {
        return String.format("Elevator %s is in state %s on level %d", id, state, level);
    }
}
