package com.kartashov.elevator.components;


import com.kartashov.elevator.entities.Elevator;
import com.kartashov.elevator.entities.Job;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PlacementTest {

    private static Elevator elevator;
    private static List<Job> jobs;

    private static final int[][] requests = {
            {1, 4, 9},
            {1, 6, 20},
            {5, 7, 19}
    };

    @BeforeClass
    public static void init() {
        elevator = new Elevator('A');
        for (int[] request : requests) {
            elevator.getJobs().add(new Job(elevator, request[0], request[1], request[2]));
        }
        elevator.getTopJob().setState(Job.State.ACTIVE);
    }

    @Test
    public void testPlacements() {
        Placement p1 = new Placement(elevator, new Request(1, 4), 20);
        assertEquals(21, p1.getScore());

        Placement p2 = new Placement(elevator, new Request(1, 6), 20);
        assertTrue(p2.getScore() > p1.getScore());

        Placement p4 = new Placement(elevator, new Request(5, 7), 20);
        p4.getJob().addPassenger();
        Placement p5 = new Placement(elevator, new Request(5, 7), 20);
        assertNotEquals(p4.getScore(), p5.getScore());
    }
}
