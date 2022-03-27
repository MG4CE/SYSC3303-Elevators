package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.IdleState;
import main.Main;

 
/**
 * Testing Elevator Movements
 * @author kevin
 *
 */
class ElevatorTest {
	static Main main;
	static Thread elevator;
	
	/**
	 * Setup main and elevators
	 */
	@BeforeAll
    public static void init() throws SocketException, UnknownHostException {
		main = new Main(23);
		elevator = new Thread(main.e);
		elevator.start();
    }
	
	/**
	 * Sleep 2 seconds before each test
	 * @throws InterruptedException
	 */
	@BeforeEach
	void sleep() throws InterruptedException {
		Thread.sleep(2000);
	}
	
	/**
	 * Test if we are in the 
	 * @throws InterruptedException
	 */
	@Test
	void testIntialState() throws InterruptedException {
		Thread.sleep(200);
		assertTrue(main.e.getEFSM().getCurrentState() instanceof IdleState);
	}
	
	/**
	 * Test if the elevator changes its id after register
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testElevatorRegister() throws IOException, InterruptedException {
		Thread.sleep(200);
		main.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		assertEquals(1, main.e.getElevatorId());
	}
	
	/**
	 * Test if the elevator starts on floor 0
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testInitialElevatorFloor() throws IOException, InterruptedException {
		Thread.sleep(500);
		assertEquals(0,main.e.getCurrentFloor());
	}
	
	/**
	 * Tests if the floor arrives at floor 1
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testGoingToNextFloor() throws IOException, InterruptedException {
		Thread.sleep(700);
		main.sendSchedulerDispatchMessage(1, 123);
		Thread.sleep(5000);
		assertEquals(1, main.e.getCurrentFloor());
	}
	
	/**
	 * Tear down
	 */
	@AfterAll
    public static void teardown() {
        elevator.interrupt();
        System.out.println("teardown");
    }

}
