package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import elevators.Scheduler;

class ElevatorTest {
	
	//Initializing test parameters 
	private Elevator testElevator;
	private Scheduler testScheduler = new Scheduler();
	private final int FLOOR = 15;
	private final int DEFAULT_FLOOR = 0;
	
	@BeforeEach
	/**
	 * Before each test, initialize the Elevator object with a scheduler
	 */
    void init() {
		testElevator = new Elevator(testScheduler);
    }
	
	@Test
	/**
	 * Test Elevators Constructor
	 */
	void testCreateElevatorDefault() {
		testElevator = new Elevator(testScheduler);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), DEFAULT_FLOOR);
	}
	
	@Test
	/**
	 * Test Constructor with Floor and test Getter for floor
	 */
	void testCreateElevator() {
		testElevator = new Elevator(testScheduler, FLOOR);
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), FLOOR);
	}
	
	@Test
	/**
	 * Test Getter for current floor
	 */
	void testGetFloor() {
		assertEquals(this.testElevator.getFloor(), DEFAULT_FLOOR);
	}
	
	@Test
	/**
	 * Test Setter for Floor
	 */
	void testChangeFloor() {
		testElevator.setFloor(FLOOR + 1);
		assertEquals(this.testElevator.getFloor(), FLOOR + 1);
	}	
}
