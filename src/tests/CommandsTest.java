package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import commands.*;
import components.LightStatus;
import elevators.Direction;
import elevators.Elevator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

/**
 * Test Class for Command class
 * @author kevin
 *
 */
class CommandsTest {
	private Command testCommand;
	
	//Initializing test parameters 
	private final int FLOOR = 15;
	private final int ELEVATOR_ID = 1;
	private final int SELECTED_FLOOR = 123;
	private final Direction DIRECTION = Direction.UP;
	private final String DIRECTION_STR = "Up";
	private final Calendar TIME = Calendar.getInstance();
	private final String TIME_STR = "00:00:00.00";
	ElevatorArrivedMessage arrivedMessage;
	ElevatorMovingMessage movingMessage;
	ElevatorRequestCommand elevatorRequestCommand;
	ElevatorSensorMessage sensorMessage;
	FloorDirectionMessage directionMessage;
	FloorRequestCommand floorRequestCommand;
	@BeforeEach

	/**
	 * Before each case create an instance of all the Command sub classes to make sure they work.
	 */
    public void init() {
		//Initialize a test command with test parameters
		testCommand = new Command(TIME);
		arrivedMessage = new ElevatorArrivedMessage(ELEVATOR_ID,FLOOR);
		movingMessage = new ElevatorMovingMessage(ELEVATOR_ID,FLOOR);
		elevatorRequestCommand = new ElevatorRequestCommand(TIME,FLOOR,ELEVATOR_ID);
		sensorMessage = new ElevatorSensorMessage(FLOOR);
		directionMessage = new FloorDirectionMessage(DIRECTION,FLOOR, LightStatus.ON);
		floorRequestCommand = new FloorRequestCommand(TIME_STR,FLOOR,DIRECTION_STR,SELECTED_FLOOR);

    }
	
	@Test
	/**
	 * Test the Command and its constructor and its methods
	 * @throws Exception
	 */
	void testCreateCommandStr() throws Exception {
		assertNotNull(testCommand);
		assertEquals(testCommand.getTimestamp(),String.format("%d:%d:%d.%d",
				TIME.get(Calendar.HOUR),
				TIME.get(Calendar.MINUTE),
				TIME.get(Calendar.SECOND),
				TIME.get(Calendar.MILLISECOND)));
	}


	@Test
	/**
	 * Test the ElevatorArrivedMessage class
	 * @throws Exception
	 */
	void testElevatorArrivedMessage() throws Exception {
		assertNotNull(arrivedMessage);
		assertEquals(arrivedMessage.getFloor(),FLOOR);
		assertEquals(arrivedMessage.getElevatorID(),ELEVATOR_ID);

		ElevatorArrivedMessage testArrivedMessage = new ElevatorArrivedMessage(2,35);
		assertNotNull(testArrivedMessage);
		assertEquals(testArrivedMessage.getFloor(),35);
		assertEquals(testArrivedMessage.getElevatorID(),2);
	}
	
	@Test
	/**
	 * Test the ElevatorMovingMessage class
	 */
	void testElevatorMovingMessage() {
		assertNotNull(movingMessage);
		assertEquals(movingMessage.getFloor(),FLOOR);
		assertEquals(movingMessage.getElevatorID(),ELEVATOR_ID);

		ElevatorMovingMessage testElevatorMovingMessage = new ElevatorMovingMessage(2,35);
		assertNotNull(testElevatorMovingMessage);
		assertEquals(testElevatorMovingMessage.getFloor(),35);
		assertEquals(testElevatorMovingMessage.getElevatorID(),2);
	}
	
	@Test
	/**
	 * Test the ElevatorRequestCommand class
	 */
	void testElevatorRequestCommand() {
		assertNotNull(elevatorRequestCommand);
		assertEquals(elevatorRequestCommand.getRequestID(),ELEVATOR_ID);
		assertEquals(elevatorRequestCommand.getFloor(),FLOOR);
		assertEquals(elevatorRequestCommand.getTimestamp(),String.format("%d:%d:%d.%d",
				TIME.get(Calendar.HOUR),
				TIME.get(Calendar.MINUTE),
				TIME.get(Calendar.SECOND),
				TIME.get(Calendar.MILLISECOND)));

		ElevatorRequestCommand testElevatorRequestCommand = new ElevatorRequestCommand(TIME,35,12);
		assertNotNull(testElevatorRequestCommand);
		assertEquals(testElevatorRequestCommand.getRequestID(),12);
		assertEquals(testElevatorRequestCommand.getFloor(),35);
	}

	@Test
	/**
	 * Test the ElevatorSensorMessage class
	 */
	void testElevatorSensorMessage() {
		assertNotNull(sensorMessage);
		assertEquals(sensorMessage.getFloor(),FLOOR);

		ElevatorSensorMessage testSensorMessage = new ElevatorSensorMessage(35);
		assertNotNull(testSensorMessage);
		assertEquals(testSensorMessage.getFloor(),35);
	}

	@Test
	/**
	 * Test the ElevatorSensorMessage class
	 */
	void testFloorDirectionMessage() {
		assertNotNull(directionMessage);
		assertEquals(directionMessage.getDirection(),DIRECTION);
		assertEquals(directionMessage.getFloor(),FLOOR);
		assertEquals(directionMessage.getLightStatus(),LightStatus.ON);

		FloorDirectionMessage testDirectionMessage = new FloorDirectionMessage(Direction.DOWN,35,LightStatus.OFF);
		assertNotNull(testDirectionMessage);
		assertEquals(testDirectionMessage.getDirection(),Direction.DOWN);
		assertEquals(testDirectionMessage.getFloor(),35);
		assertEquals(testDirectionMessage.getLightStatus(),LightStatus.OFF);

	}

	@Test
	/**
	 * Test the ElevatorSensorMessage class
	 */
	void testFloorRequestCommand() {
		assertNotNull(floorRequestCommand);
		assertEquals(floorRequestCommand.getFloor(),FLOOR);
		assertEquals(floorRequestCommand.getRequestID(),SELECTED_FLOOR);
		assertEquals(floorRequestCommand.getDirection(),DIRECTION);

		FloorRequestCommand testFloorRequestCommand = new FloorRequestCommand(TIME_STR,35,"Down",12);
		assertNotNull(testFloorRequestCommand);
		assertEquals(testFloorRequestCommand.getFloor(),35);
		assertEquals(testFloorRequestCommand.getRequestID(),12);
		assertEquals(testFloorRequestCommand.getDirection(),Direction.DOWN);
	}
}
