package com.kartashov.elevator.components;

/**
 * Single journey request.
 *
 * Even when we get a group request to travel, we treat it as a sequence of individual requests,
 * and split them in the optimal manner.
 */
public class Request {

    private final int from;
    private final int to;

    /**
     * Create new journey request.
     *
     * @param from the starting level
     * @param to the terminal level
     * @throws java.lang.IllegalArgumentException if the starting and terminal levels are the same
     *         or negative level used
     */
    public Request(int from, int to) {
        if (from == to) {
            throw new IllegalArgumentException("The journey cannot start and end at the same level");
        }
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Levels cannot be negative");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Get the starting level of the journey
     *
     * @return the starting level
     */
    public int getFrom() {
        return from;
    }

    /**
     * Get the terminal level of the journey
     *
     * @return the terminal level
     */
    public int getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("Request to go from %d to %d", from, to);
    }
}
