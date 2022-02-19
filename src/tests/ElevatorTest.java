package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import commands.ElevatorDispatchCommand;
import commands.ElevatorFloorSensorMessage;
import elevators.Direction;
import elevators.Elevator;
import scheduler.Scheduler;

class ElevatorTest {
	Elevator elevator;
	Scheduler s;
	
	@BeforeEach
	void init() {
		s = new Scheduler();
		elevator = new Elevator(s,1);
		s.setElevator(elevator);
	}
	
	@Test
	void testEnterBoardingState() {
		ElevatorDispatchCommand cmd = new ElevatorDispatchCommand(0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.BOARDING, elevator.getCurrentState());
	}

	@Test
	void testEnterMovingState() {
		ElevatorDispatchCommand cmd = new ElevatorDispatchCommand(10);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, elevator.getCurrentState());
	}

	@Test
	void testEnterArriving() {
		testEnterMovingState();
		ElevatorFloorSensorMessage cmd = new ElevatorFloorSensorMessage(1, 0);
		elevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, elevator.getCurrentState());
	}
	
}
