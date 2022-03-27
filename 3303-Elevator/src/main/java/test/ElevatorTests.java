package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.BoardingState;
import elevators.IdleState;
import elevators.MovingState;
import main.Main;

 
/**
 * Testing Elevator Movements
 * @author kevin
 *
 */
class ElevatorTest {
	static Main main,main1, main2,main3,main4,main5,main6,main7;
	static Thread elevator,elevator1,elevator2, elevator3,elevator4, elevator5,elevator6;
	
	/**
	 * Setup main and elevators
	 */
	@BeforeAll
    public static void init() throws SocketException, UnknownHostException {
		
		/**
		 * Main classes used as pretend to be a scheduler
		 * Can register and schedule 1 elevator
		 */
		main = new Main(23,24);
	    main1 = new Main(25,26);
	    main2 = new Main(30,31);
	    main3 = new Main(32,33);
	    main4 = new Main(34,35);
	    main5 = new Main(36,37);
	    main6 = new Main(38,39);
		
	    /**
	     * Elevator threads running the elevator that was made in each main
	     */
		elevator = new Thread(main.e);
		elevator1 = new Thread(main1.e);
		elevator2= new Thread(main2.e);
		elevator3 = new Thread(main3.e);
		elevator4 = new Thread(main4.e);
		elevator5 = new Thread(main5.e);
		elevator6 = new Thread(main6.e);
		
		/**
		 * Start each thread
		 */
		elevator.start();
		elevator1.start();
		elevator2.start();
		elevator3.start();
		elevator4.start();
		elevator5.start();
		elevator6.start();
		
		
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
	void testIntialIdleState() throws InterruptedException {
		Thread.sleep(200);
		assertTrue(main1.e.getEFSM().getCurrentState() instanceof IdleState);
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
		assertEquals(0,main1.e.getCurrentFloor());
	}
	
	/**
	 * Tests if the floor arrives at floor 1
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testGoingToNextFloor() throws IOException, InterruptedException {
		main1.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		main1.sendSchedulerDispatchMessage(1, 123);
		System.out.println(main.e.getCurrentFloor());
		Thread.sleep(5000);
		assertEquals(1, main1.e.getCurrentFloor());
	}
	
	/**
	 * Tests that an elevator is in the moving state after it is dispatched to a floor
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testMovingState() throws IOException, InterruptedException {
		main2.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		main2.sendSchedulerDispatchMessage(2, 123);
		Thread.sleep(3000);
		System.out.println(main2.e.getEFSM().getCurrentState());
		assertTrue(main2.e.getEFSM().getCurrentState() instanceof MovingState);
	}
	
	/**
	 * Tests if the elevator goes to the boarding state after it arrives
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testBoardingState() throws IOException, InterruptedException {
		main3.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		main3.sendSchedulerDispatchMessage(1, 123);
		Thread.sleep(5000);
		assertTrue(main3.e.getEFSM().getCurrentState() instanceof BoardingState);
	}
	
	/**
	 * Tests if the elevator boarding state goes to idle after 4 seconds of elevator idling
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testBoardingStateTimeoutToIdle() throws IOException, InterruptedException {
		main4.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		main4.sendSchedulerDispatchMessage(1, 123);
		Thread.sleep(10000);
		assertTrue(main4.e.getEFSM().getCurrentState() instanceof IdleState);
	}
	
	/**
	 * Tests if an elevator can go to up multiple floors
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testScheduling2UpFloors() throws IOException, InterruptedException {
		main5.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		main5.sendSchedulerDispatchMessage(1, 123);
		Thread.sleep(3200 * 1);
		main5.sendSchedulerDispatchMessage(4, 123);
		System.out.println(main.e.getCurrentFloor());
		Thread.sleep(3200 * 5);
		assertEquals(4, main5.e.getCurrentFloor());
	}
	
	/**
	 * Tests if an elevator can go to up to a floor then back down
	 * Start 0 - go to 4 - go to 1
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	void testScheduling1Up1DownFloors() throws IOException, InterruptedException {
		main6.sendElevatorRegisterMessage(1);
		Thread.sleep(200);
		main6.sendSchedulerDispatchMessage(4, 123);
		Thread.sleep(3200 * 5);
		main6.sendSchedulerDispatchMessage(1, 123);
		Thread.sleep(3200 * 5);
		assertEquals(1, main6.e.getCurrentFloor());
	}
  
	/**
	 * Tear down
	 */
	@AfterAll
    public static void teardown() {
		main.e.killElevator();
		main1.e.killElevator();
		main2.e.killElevator();
		main3.e.killElevator();
		main4.e.killElevator();
		main5.e.killElevator();
		main6.e.killElevator();
        elevator.interrupt();
        elevator1.interrupt();
        elevator2.interrupt();
        elevator3.interrupt();
        elevator4.interrupt();
        elevator5.interrupt();
        elevator6.interrupt();
        System.out.println("teardown");
    }

}
