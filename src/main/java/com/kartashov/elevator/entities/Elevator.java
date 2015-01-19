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
    public enum State { IDLE, CALLING, BOARDING, CARRYING, EXITING }

    @Id
    private Character id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "elevator")
    private List<com.kartashov.elevator.entities.Job> jobs = new ArrayList<>();

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

    public State getState() {
        return state;
    }

    public Integer getLevel() {
        return level;
    }

    public boolean update(Job job) {
        switch (state) {
            case IDLE:
                state = State.CALLING;
                return false;
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
    public boolean equals(Object o) {
        return o instanceof Elevator && ((Elevator) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Elevator %s is in state %s on level %d", id, state, level);
    }
}
