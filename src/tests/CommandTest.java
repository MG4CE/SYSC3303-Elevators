package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import elevators.Command;

/**
 * Test Class for Command class
 * @author kevin
 *
 */
class CommandTest {
	private Command testCommand;
	
	//Initializing test parameters 
	private final int FLOOR = 15;
	private final int SELECTED_FLOOR = 123;
	private final Command.Direction DIRECTION = Command.Direction.UP;
	private final String DIRECTION_STR = "Up";
	private final Calendar TIME = Calendar.getInstance();
	private final String TIME_STR = "00:00:00.00";
	
	@BeforeEach
	/**
	 * Before each case have the test command ready
	 */
    public void init() {
		//Initialize a test command with test parameters
		testCommand = new Command(TIME, FLOOR, DIRECTION, SELECTED_FLOOR);
    }
	
	@Test
	/**
	 * Test the the commands constructor with parsed string variables
	 * @throws Exception
	 */
	void testCreateCommandStr() throws Exception {
		testCommand = new Command(TIME_STR, FLOOR, DIRECTION_STR, SELECTED_FLOOR);
		assertNotNull(testCommand);
	}
	
	@Test
	/**
	 * Test Command constructor with intialized variables
	 * @throws Exception
	 */
	void testCreateCommandTimeDir() throws Exception {
		testCommand = new Command(TIME, FLOOR, DIRECTION, SELECTED_FLOOR);
		assertNotNull(testCommand);
	}
	
	@Test
	/**
	 * Test Getter for Floor
	 */
	void testGetFloor() {
		assertEquals(testCommand.getFloor(), FLOOR);
	}
	
	@Test
	/**
	 * Test Getter for Direction enum
	 */
	void testGetDirection() {
		assertEquals(testCommand.getDirection(), DIRECTION);
	}

	@Test
	/**
	 * Test getter for selected Floor
	 */
	void testGetSelectedFloor() {
		assertEquals(testCommand.getSelectedFloor(), SELECTED_FLOOR);
	}
}
