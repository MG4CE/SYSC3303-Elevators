package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.ExternalFloorBtnCommand;
import commands.InteriorElevatorBtnCommand;
import elevators.Direction;
import elevators.Elevator;
import scheduler.Scheduler;
import scheduler.Scheduler.controlState;

class SchedulerTest {
	Scheduler scheduler;
	Elevator elevator;
	
	@BeforeEach
	void init() {
		scheduler = new Scheduler();
		elevator = new Elevator(scheduler, 123);
		scheduler.setElevator(elevator);
	}
	
	@Test
	void testElevatorAdded() {
		assertTrue(scheduler.hasElevator());
	}
	
	@Test
	void testExternalButtonClick() {
		ExternalFloorBtnCommand cmd = new ExternalFloorBtnCommand(2, Direction.UP, 123); 
		scheduler.updateControlFSM(cmd);
		assertEquals(controlState.DISPATCH, scheduler.getState());
	}
	
	@Test
	void testInternalButtonClick() {
		InteriorElevatorBtnCommand cmd = new InteriorElevatorBtnCommand(2, 123); 
		scheduler.updateControlFSM(cmd);
		assertEquals(controlState.DISPATCH, scheduler.getState());
	}
	
	@Test
	void testFloorAdded() {
		InteriorElevatorBtnCommand cmd = new InteriorElevatorBtnCommand(2, 123); 
		scheduler.updateControlFSM(cmd);
		assertEquals(2, scheduler.getNextFloor());
	}
	
	

}
