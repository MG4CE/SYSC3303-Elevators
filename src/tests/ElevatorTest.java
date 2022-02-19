package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import commands.ElevatorFloorSensorMessage;
import elevators.Elevator;

class ElevatorTest {
	Elevator elevator;
	
	@BeforeEach
	void init() {
		elevator = new Elevator();
	}
	
	@Test
	void testEnterBoardingState() {
		InteriorElevatorBtnCommand cmd = new InteriorElevatorBtnCommand(0, 0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.BOARDING, elevator.getCurrentState());
	}

	@Test
	void testEnterMovingState() {
		InteriorElevatorBtnCommand cmd = new InteriorElevatorBtnCommand(10, 0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, elevator.getCurrentState());
	}

	@Test
	void testEnterArriving() {
		testEnterMovingState();
		elevator.setCurrentFloor(10);
		ElevatorFloorSensorMessage cmd = new ElevatorFloorSensorMessage(0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, elevator.getCurrentState());
	}
	
}
