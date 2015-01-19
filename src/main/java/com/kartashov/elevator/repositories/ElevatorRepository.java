package com.kartashov.elevator.repositories;

import com.kartashov.elevator.entities.Elevator;
import org.springframework.data.repository.CrudRepository;

public interface ElevatorRepository extends CrudRepository<Elevator, Character> {
}
