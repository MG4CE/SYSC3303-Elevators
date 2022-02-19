package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import scheduler.Scheduler;

class SchedulerTest {
	Scheduler scheduler;
	Elevator elevator;
	
	@BeforeEach
	void init() {
		elevator = new Elevator();
		scheduler = new Scheduler(elevator);
	}
	@Test
	void testEnterDispatchState() {
		ElevatorRequestCommand;
		schedulerPutCommand
	}

}
