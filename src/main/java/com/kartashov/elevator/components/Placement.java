package com.kartashov.elevator.components;

import com.kartashov.elevator.entities.Elevator;
import com.kartashov.elevator.entities.Job;

/**
 * Placement computes the best score of a new request in the job queue.
 * The active jobs are not modified nor interrupted.
 * Outstanding jobs accept an additional passenger only if her journey completely coincides with the job.
 *
 * In order to compute the score of every position we compute the running sum of traveling times of all jobs.
 *
 * The logic of this is based on the simple (and imprecise) observation that for a person on level 9,
 * a job queue of 10 requests to go from 9th to 8th and back, is preferred to the job queue
 * with just 1 job to go from 9 to 1.
 *
 */
class Placement implements Comparable<Placement> {

    private Job selectedJob;
    private int score;
    private int level;

    public Placement(Elevator elevator, Request request, int maxCapacity) {
        level = elevator.getLevel();
        for (Job job : elevator.getJobs()) {
            if (job.getState() == Job.State.ACTIVE) {
                switch (elevator.getState()) {
                    case IDLE:
                    case CALLING:
                        moveTo(job.getFrom());
                    case BOARDING:
                    case CARRYING:
                        moveTo(job.getTo());
                    case EXITING:
                }
            } else {
                moveTo(job.getFrom());
                if (accepts(job, request, maxCapacity)) {
                    selectedJob = job;
                    break;
                } else {
                    moveTo(job.getTo());
                }
            }
        }
        if (selectedJob == null) {
            selectedJob = new Job(elevator, request);
            elevator.getJobs().add(selectedJob);
            moveTo(request.getFrom());
        }
    }

    public Job getJob() {
        return selectedJob;
    }

    @Override
    public int compareTo(Placement o) {
        return Integer.compare(score, o.score);
    }

    @Override
    public String toString() {
        return String.format("Score is %d", score);
    }

    int getScore() {
        return score;
    }

    private boolean accepts(Job job, Request request, int maxCapacity) {
        return job.getFrom() == request.getFrom()
                && job.getTo() == request.getTo()
                && job.getPassengers() < maxCapacity;
    }

    private void moveTo(int to) {
        score += Math.abs(level - to);
        level = to;
    }
}
