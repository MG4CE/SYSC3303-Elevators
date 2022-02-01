package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import elevators.Command;


class CommandTest {
	private Command testCommand;
	
	private final int FLOOR = 15;
	private final Command.Direction DIRECTION = Command.Direction.UP;
	private final int ELEVATOR_ID = 123;
	

	@Test
	void testCreateCommand() {
		testCommand = new Command(Calendar.getInstance(), FLOOR, DIRECTION, ELEVATOR_ID);
		assertNotNull(testCommand);
	}
	
	@Test
	void testGetFloor() {
		assertEquals(testCommand.getFloor(), FLOOR);
	}
	
	@Test
	void testGetDirection() {
		assertEquals(testCommand.getDirection(), DIRECTION);
	}

	@Test
	void testGetElevatorID() {
		assertEquals(testCommand.getElevatorID(), ELEVATOR_ID);
	}
}
