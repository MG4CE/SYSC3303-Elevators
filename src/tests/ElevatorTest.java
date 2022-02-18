package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.ElevatorRequestCommand;
import commands.ElevatorSensorMessage;
import elevators.Direction;
import elevators.Elevator;
import stateMachines.ElevatorFSM;
import stateMachines.ElevatorFSM.State;

class ElevatorTest {
	Elevator elevator;
	
	@BeforeEach
    public void init() {
		elevator = new Elevator();
    }
	
	
	/*
	 * Test if FSM goes from idle to boarding
	 */
	@Test
	void testTransitionIdleToBoarding() {
		ElevatorRequestCommand cmd = new ElevatorRequestCommand("00:00:00.0", 0, 123);
		elevator.onCommand(cmd); // Attempt to put FSM in BOARDING state
		assertEquals(State.BOARDING, elevator.getElevatorState());
	}

	/*
	 * Test if FSM goes from boarding to idle
	 */
	@Test
	void testTransitionBoardingToIdle() {
		testTransitionIdleToBoarding();
		ElevatorRequestCommand c2 = new ElevatorRequestCommand("00:00:00.0", 2, 123);
		elevator.onCommand(c2); // Attempt to put FSM in IDLE state
		assertEquals(State.IDLE, elevator.getElevatorState());
	}
	
	/*
	 * Test if FSM goes to moving state
	 */
	@Test
	void testTransitionIdleToMoving() {
		int destFloor = 10;
		ElevatorRequestCommand c1 = new ElevatorRequestCommand("00:00:00.0", destFloor, 123);
		elevator.onCommand(c1); // Attempt to put FSM in moving state
		assertEquals(elevator.getDestinationFloor(), destFloor);
		assertEquals(State.MOVING, elevator.getElevatorState());
	}
	
	/*
	 * Test if FSM updates floor if Higher priority request comes in
	 */
	@Test
	void testHighPriorityRequestMoving() {
		int destFloor = 5;
		testTransitionIdleToMoving(); // set elevator to move towards floor 10
		ElevatorRequestCommand c1 = new ElevatorRequestCommand("00:00:00.0", destFloor, 123);
		// send floor in middle
		elevator.onCommand(c1);
		assertEquals(elevator.getDestinationFloor(), destFloor);
		assertEquals(State.MOVING, elevator.getElevatorState());
	}
	
	/*
	 * Test if FSM updates from moving to ARRIVING state
	 */
	@Test
	void testTransitionMovingToArriving() {
		testTransitionIdleToMoving();
		int floor = elevator.getDestinationFloor() -1; // one floor under destination floor
		elevator.setCurrentFloor(floor);
		ElevatorSensorMessage cmd = new ElevatorSensorMessage(floor);
		elevator.onCommand(cmd);
		assertEquals(State.ARRIVING, elevator.getElevatorState());
	}
	
	@Test
	void testTransitionArrivingToBoarding() {
		testTransitionMovingToArriving();
		int floor = elevator.getDestinationFloor();
		elevator.setCurrentFloor(floor);
		ElevatorSensorMessage cmd = new ElevatorSensorMessage(floor);
		elevator.onCommand(cmd);
		assertEquals(State.BOARDING, elevator.getElevatorState());
	}
	

	
}
