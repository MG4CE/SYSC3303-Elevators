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
 * @author Golan
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
    private ElevatorArrivedMessage arrivedMessage;
    private ElevatorMovingMessage movingMessage;
    private ElevatorFloorSensorMessage elevatorRequestCommand;
    @BeforeEach

    /**
     * Before each case create an instance of all the Command sub classes to make sure they work.
     */
    public void init() {
        //Initialize a test command with test parameters
        testCommand = new Command(TIME);
        arrivedMessage = new ElevatorArrivedMessage(ELEVATOR_ID,FLOOR, SELECTED_FLOOR);
        movingMessage = new ElevatorMovingMessage(ELEVATOR_ID,FLOOR,DIRECTION);
        elevatorRequestCommand = new ElevatorFloorSensorMessage(FLOOR,ELEVATOR_ID);


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

        ElevatorArrivedMessage testArrivedMessage = new ElevatorArrivedMessage(2,35,SELECTED_FLOOR);
        assertNotNull(testArrivedMessage);
        assertEquals(testArrivedMessage.getFloor(),35);
        assertEquals(testArrivedMessage.getElevatorID(),2);
    }

}