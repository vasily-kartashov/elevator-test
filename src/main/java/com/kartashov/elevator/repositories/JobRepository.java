package com.kartashov.elevator.repositories;

import com.kartashov.elevator.entities.Elevator;
import com.kartashov.elevator.entities.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, Integer> {

    Job findTopByElevator(Elevator elevator);

}
