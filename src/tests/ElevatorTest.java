package tests;

import static org.junit.jupiter.api.Assertions.*;

import components.LightStatus;
import components.VerticalLocation;
import elevators.*;
import elevators.ArrivalSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import stateMachines.Scheduler;
import stateMachines.Scheduler;

class ElevatorTest {
	
	//Initializing test parameters 
	private Elevator testElevator;
	private Scheduler testScheduler = new Scheduler();
	private final int FLOOR = 15;
	private final int DEFAULT_FLOOR = 0;
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
		testElevator = new Elevator(testScheduler);
		arrivalSensor = new ArrivalSensor(FLOOR);
		door = new Door();
		elevatorButton = new ElevatorButton(FLOOR);
		elevatorButtonLamp = new ElevatorButtonLamp(FLOOR);
		motor = new Motor();
    }
	
	@Test
	/**
	 * Test the Elevator class
	 */
	void testElevator() {
		assertNotNull(testElevator);
		assertEquals(testElevator.getFloor(), DEFAULT_FLOOR);
		assertNotEquals(testElevator.getFloor(), FLOOR);
		testElevator.setFloor(35);
		assertEquals(testElevator.getFloor(),35);

		Elevator testElevator2 = new Elevator(testScheduler);
		assertNotNull(testElevator2);
		testElevator2.setFloor(25);
		assertEquals(testElevator2.getFloor(),25);
	}
	
	@Test
	/**
	 * Test the ArrivalSensor class
	 */
	void testArrivalSensor() {
		assertNotNull(arrivalSensor);
		assertEquals(arrivalSensor.getFloor(),FLOOR);
		assertEquals(arrivalSensor.getLocation(),FLOOR*DISTANCE_BETWEEN_FLOORS_METERS);

		ArrivalSensor testArrivalSensor = new ArrivalSensor(35);
		assertNotNull(testArrivalSensor);
		assertEquals(testArrivalSensor.getFloor(),35);
		assertEquals(testArrivalSensor.getLocation(),35*DISTANCE_BETWEEN_FLOORS_METERS);
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
