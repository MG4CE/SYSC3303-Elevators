package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevators.Elevator;
import elevators.Scheduler;

class ElevatorTest {

	private Elevator testElevator;
	private Scheduler testScheduler = new Scheduler();
	private boolean ISFINISHED = false;
	private final int FLOOR = 15;
	
	@Test
	void testCreateElevatorDefault() {
		testElevator = new Elevator(testScheduler);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), FLOOR);
	}
	
	@Test
	void testCreateElevator() {
		testElevator = new Elevator(testScheduler, FLOOR, ISFINISHED);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), FLOOR);
	}
	
	@Test
	void testGetFloor() {
		assertEquals(this.testElevator.getFloor(), FLOOR);
	}
	
	@Test
	void testChangeFloor() {
		this.testElevator.setFloor(FLOOR + 1);
		assertEquals(this.testElevator.getFloor(), FLOOR + 1);
	}	
}
