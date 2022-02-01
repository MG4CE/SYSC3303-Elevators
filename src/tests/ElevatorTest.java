package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevators.Elevator;
import elevators.Scheduler;

class ElevatorTest {

	private Elevator testElevator;
	private Scheduler testScheduler;
	
	private final int FLOOR = 15;
	
	@Test
	void testCreateElevator() {
		testScheduler = new Scheduler();
		testElevator = new Elevator(testScheduler, FLOOR);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), FLOOR);
	}

	@Test
	void testCreateElevatorDefault() {
		testScheduler = new Scheduler();
		testElevator = new Elevator(testScheduler);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), 0); // default val
	}
	
	@Test
	void testSetFloor() {
		this.testElevator.setFloor(FLOOR);
		assertEquals(this.testElevator.getFloor(), FLOOR);
	}	

}
