package tests;

import static org.junit.jupiter.api.Assertions.*;


import components.LightStatus;
import elevators.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import commands.ElevatorRequestCommand;
import commands.ElevatorSensorMessage;


/**
 * The test class for all the classes that are within the 'elevators' package
 */
class ElevatorTest {

	//Initializing test parameters
	private Elevator testElevator;
	private final int FLOOR = 15;
	private final int DISTANCE_BETWEEN_FLOORS_METERS = 4;
	ArrivalSensor arrivalSensor;
	Door door;
	ElevatorButton elevatorButton;
	ElevatorButtonLamp elevatorButtonLamp;
	Motor motor;

	@BeforeEach
	/**
	 * Before each test, initialize the Elevator object with a scheduler and all other elevator type classes
	 */
    void init() {
		testElevator = new Elevator();
		arrivalSensor = new ArrivalSensor(FLOOR);
		door = new Door();
		elevatorButton = new ElevatorButton(FLOOR);
		elevatorButtonLamp = new ElevatorButtonLamp(FLOOR);
		motor = new Motor();
    }


	@Test
	void testEnterBoardingState() {
		ElevatorRequestCommand cmd = new ElevatorRequestCommand(0, 0);
		testElevator.updateFSM(cmd);
		assertEquals(Elevator.State.BOARDING, testElevator.getCurrentState());
	}

	@Test
	/**
	 * Test the Motor class
	 */
	void testEnterArriving() {
		testEnterMovingState();
		testElevator.setCurrentFloor(10);
		ElevatorSensorMessage cmd = new ElevatorSensorMessage(0);
		testElevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, testElevator.getCurrentState());
	}

	void testEnterMovingState() {
		ElevatorRequestCommand cmd = new ElevatorRequestCommand(10, 0);
		testElevator.updateFSM(cmd);
		assertEquals(Elevator.State.MOVING, testElevator.getCurrentState());
	}

	@Test
	/**
	 * Test the ArrivalSensor class
	 */
	void testArrivalSensor() {
		assertNotNull(arrivalSensor);
		assertEquals(arrivalSensor.getFloor(), FLOOR);
		assertEquals(arrivalSensor.getLocation(), FLOOR * DISTANCE_BETWEEN_FLOORS_METERS);

		ArrivalSensor testArrivalSensor = new ArrivalSensor(35);
		assertNotNull(testArrivalSensor);
		assertEquals(testArrivalSensor.getFloor(), 35);
		assertEquals(testArrivalSensor.getLocation(), 35 * DISTANCE_BETWEEN_FLOORS_METERS);
	}



	@Test
	/**
	 * Test the Door class
	 */
	void testDoor() {
		assertNotNull(door);
		assertEquals(door.isDoorOpen(), false);
		door.openDoor();
		assertEquals(door.isDoorOpen(),true);
		door.closeDoor();
		assertEquals(door.isDoorOpen(),false);

		Door testDoor = new Door();
		assertNotNull(testDoor);
	}

	@Test
	/**
	 * Test the ElevatorButton class
	 */
	void testElevatorButton() {
		assertNotNull(elevatorButton);
		assertEquals(elevatorButton.getFloor(),FLOOR);

		ElevatorButton testElevatorButton = new ElevatorButton(35);
		assertNotNull(testElevatorButton);
		assertEquals(testElevatorButton.getFloor(),35);
	}

	@Test
	/**
	 * Test the ElevatorButtonLamp class
	 */
	void testElevatorButtonLamp() {
		assertNotNull(elevatorButtonLamp);
		assertEquals(elevatorButtonLamp.getFloor(),FLOOR);
		assertEquals(elevatorButtonLamp.getLightStatus(), LightStatus.OFF);
		elevatorButtonLamp.turnOn();
		assertEquals(elevatorButtonLamp.getLightStatus(), LightStatus.ON);
		elevatorButtonLamp.turnOff();
		assertEquals(elevatorButtonLamp.getLightStatus(), LightStatus.OFF);
	}

	@Test
	/**
	 * Test the Motor class
	 */
	void testMotor() {
		assertNotNull(motor);
		assertEquals(motor.isMessageReady(),false);
	}



}
