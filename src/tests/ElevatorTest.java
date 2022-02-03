package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import elevators.Scheduler;

class ElevatorTest {

	private Elevator testElevator;
	private Scheduler testScheduler = new Scheduler();
	private final int FLOOR = 15;
	private final int DEFAULT_FLOOR = 0;
	
	@BeforeEach
    void init() {
		testElevator = new Elevator(testScheduler);
    }
	
	@Test
	void testCreateElevatorDefault() {
		testElevator = new Elevator(testScheduler);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), DEFAULT_FLOOR);
	}
	
	@Test
	void testCreateElevator() {
		testElevator = new Elevator(testScheduler, FLOOR);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), FLOOR);
	}
	
	@Test
	void testGetFloor() {
		assertEquals(this.testElevator.getFloor(), DEFAULT_FLOOR);
	}
	
	@Test
	void testChangeFloor() {
		testElevator.setFloor(FLOOR + 1);
		assertEquals(this.testElevator.getFloor(), FLOOR + 1);
	}	
}
