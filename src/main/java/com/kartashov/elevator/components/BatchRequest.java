package com.kartashov.elevator.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Iterator;

/**
 * Batch journey request.
 */
public class BatchRequest extends Request implements Iterable<Request> {

    private final int passengers;

    /**
     * Create batch request
     *
     * @param from starting level
     * @param to terminal level
     * @param passengers number of passengers
     * @throws java.lang.IllegalArgumentException if non positive number of passengers is given
     */
    @JsonCreator
    public BatchRequest(@JsonProperty("from") int from,
                        @JsonProperty("to") int to,
                        @JsonProperty("passengers") int passengers) {
        super(from, to);
        if (passengers < 1) {
            throw new IllegalArgumentException("Number of passengers must be a positive number");
        }
        this.passengers = passengers;
    }

    /**
     * Get number of passengers in the request
     *
     * @return number of passengers
     */
    public int getPassengers() {
        return passengers;
    }

    @Override
    public Iterator<Request> iterator() {

        return new Iterator<Request>() {

            private int counter = 1;

            @Override
            public boolean hasNext() {
                return counter <= passengers;
            }

            @Override
            public Request next() {
                counter++;
                return new Request(getFrom(), getTo());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String toString() {
        return String.format("Request to move %d passengers from %d to %d", passengers, getFrom(), getTo());
    }
}
