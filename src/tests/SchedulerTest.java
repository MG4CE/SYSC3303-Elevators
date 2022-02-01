package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.Scheduler;

class SchedulerTest {
	private Scheduler testScheduler;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testCreateScheduler() {
		testScheduler = new Scheduler();
		assertNotNull(testScheduler);
	}
	
	void testAddCommand() {
		Command cmd = new Command();
		testScheduler.addCommand(null);
	}

}
