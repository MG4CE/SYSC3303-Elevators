package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.ElevatorMovingMessage;
import commands.ElevatorRequestCommand;
import commands.ElevatorSensorMessage;
import elevators.Elevator;

class ElevatorTest {
	Elevator elevator;
	
	@BeforeEach
	void init() {
		elevator = new Elevator();
	}
	
	@Test
	void testEnterBoardingState() {
		ElevatorRequestCommand cmd = new ElevatorRequestCommand(0, 0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.BOARDING, elevator.getCurrentState());
	}

	@Test
	void testEnterMovingState() {
		ElevatorRequestCommand cmd = new ElevatorRequestCommand(10, 0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, elevator.getCurrentState());
	}

	@Test
	void testEnterArriving() {
		testEnterMovingState();
		elevator.setCurrentFloor(10);
		ElevatorSensorMessage cmd = new ElevatorSensorMessage(0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, elevator.getCurrentState());
	}
	
}
