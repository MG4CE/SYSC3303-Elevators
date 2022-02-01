package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import elevators.Command;
import elevators.Scheduler;

class SchedulerTest {
	private Scheduler testScheduler;
	
	@Test
	void testCreateSchedulerDefault() {
		testScheduler = new Scheduler();
		assertNotNull(testScheduler);
	}
	
	@Test
	void testCreateSchedulerWithQueue() {
		Queue<Command> commandQueue = new LinkedList<Command>(); // use empty queue
		testScheduler = new Scheduler(commandQueue);
		assertNotNull(testScheduler);
		assertTrue(this.testScheduler.getCommandQueueSize() == 0);
	}

	@Test
	void testAddCommand() {
		Command cmd = new Command(Calendar.getInstance(), 1, Command.Direction.UP, 123);
		assertNotNull(cmd);
		this.testScheduler.addCommand(cmd);
		assertTrue(this.testScheduler.getCommandQueueSize() == 1); // should have 1 item in 
	}
	
	@Test
	void testRemoveCommand() {
		Command cmd = this.testScheduler.getCommand();
		assertTrue(this.testScheduler.getCommandQueueSize() == 0); // should be empty
		assertNotNull(cmd);
	}
	
}
