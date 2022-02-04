package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.Command;
import elevators.Elevator;
import elevators.Scheduler;

/**
 * Test class for Scheduler Class
 *
 */
class SchedulerTest {
	//Initializing test parameters 
	private Scheduler testScheduler;
	
	@BeforeEach
	/**
	 * Before each test create the Scheduler object
	 */
    void init() {
		testScheduler = new Scheduler();
    }
	
	@Test
	/**
	 * Test the constructor
	 */
	void testCreateSchedulerDefault() {
		testScheduler = new Scheduler();
		assertNotNull(testScheduler);
	}
	
	@Test
	/**
	 * Test constructor with Queue parameter
	 */
	void testCreateSchedulerWithQueue() {
		Queue<Command> commandQueue = new LinkedList<Command>(); // use empty queue
		testScheduler = new Scheduler(commandQueue);
		assertNotNull(testScheduler);
		assertTrue(this.testScheduler.getCommandQueueSize() == 0);
	}

	@Test
	/**
	 * Test adding a command to the Queue
	 */
	void testAddCommand() {
		Command cmd = new Command(Calendar.getInstance(), 1, Command.Direction.UP, 123);
		assertNotNull(cmd);
		this.testScheduler.addCommand(cmd);
		assertTrue(this.testScheduler.getCommandQueueSize() == 1); // should have 1 item in 
	}
	
	@Test
	/**
	 * Test removing a command from the Queue
	 */
	void testRemoveCommand() {
		Command cmd = new Command(Calendar.getInstance(), 1, Command.Direction.UP, 123);
		this.testScheduler.addCommand(cmd);
		cmd = this.testScheduler.getCommand();
		assertNotNull(cmd);
		assertTrue(this.testScheduler.getCommandQueueSize() == 0); // should be empty
		assertNotNull(cmd);
	}
	
}
