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
	private final int SELECTED_FLOOR = 123;
	private final Command.Direction DIRECTION = Command.Direction.UP;
	private final String DIRECTION_STR = "UP";
	private final Calendar TIME = Calendar.getInstance();
	private final String TIME_STR = "00:00:00.00";
	
	@Test
	void testCreateCommandStr() {
		testCommand = new Command(TIME_STR, FLOOR, DIRECTION_STR, SELECTED_FLOOR);
		assertNotNull(testCommand);
	}
	
	@Test
	void testCreateCommandTimeDir() {
		testCommand = new Command(TIME, FLOOR, DIRECTION, SELECTED_FLOOR);
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
	void testSelectedFloor() {
		assertEquals(testCommand.getSelectedFloor(), SELECTED_FLOOR);
	}
}
