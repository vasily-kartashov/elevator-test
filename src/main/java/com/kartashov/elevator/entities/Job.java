package com.kartashov.elevator.entities;

import com.kartashov.elevator.components.Request;

import javax.persistence.*;

@Entity
public class Job {

    public enum State { ACTIVE, OUTSTANDING }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Elevator elevator;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false, name = "from_level")
    private Integer from = 0;

    @Column(nullable = false, name = "to_level")
    private Integer to = 0;

    @Column(nullable = false)
    private Integer passengers = 0;

    public Job() {
        this.state = State.OUTSTANDING;
        this.passengers = 0;
    }

    public Job(Elevator elevator, Request request) {
        this();
        this.elevator = elevator;
        this.from = request.getFrom();
        this.to = request.getTo();
    }

    public Job(Elevator elevator, int from, int to, int passengers) {
        this();
        this.elevator = elevator;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public void setElevator(Elevator elevator) {
        this.elevator = elevator;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void addPassenger() {
        passengers++;
    }

    @Override
    public String toString() {
        return String.format("Job for elevator %s to cary %d from %d to %d, current state %s",
                elevator.getId(), passengers, from, to, state);
    }
}
