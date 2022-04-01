package scheduler;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.SocketException;

import org.junit.jupiter.api.*;

import message.Button;


/**
 * Testing the Scheduler
 * @Golan
 */
public class SchedulerTests {
    static ElevatorControl e1, e2, e3 ,e4 ,e5;
    static Scheduler s1, s2, s3, s4, s5;

    @BeforeAll
    public static void init() throws SocketException {
        s1 = new Scheduler(3000,5);
        s2 = new Scheduler(3001,5);
        s3 = new Scheduler(3002,5);
        s4 = new Scheduler(3003,5);
        s5 = new Scheduler(3004,5);
        e1 = new ElevatorControl(3000,3,0,null,s1);
        e2 = new ElevatorControl(3001,3,0,null,s2);
        e3 = new ElevatorControl(3002,3,0,null,s3);
        e4 = new ElevatorControl(3003,3,0,null,s4);
        e5 = new ElevatorControl(3004,3,0,null,s5);
    }

    /**
     * Test the VerifyElevatorTopRequests method
     */
    @Test
    public void testVerifyElevatorTopRequests()
    {
        s3.addToElevators(e3);
        assertEquals(e3.peekTopRequest(),null);
    }

    @Test
    /**
     * Test the assignBestElevatorClass
     */
    public void testAssignBestElevator() throws IOException {
        ElevatorControl e2;
        e2 = ElevatorSelection.assignBestElevator(new ElevatorRequest(1,0, Button.EXTERIOR), s1.elevators, s1.numFloors);
        assertEquals(e2,null);
        s1.addToElevators(e1);
        e1.addDestination(new ElevatorRequest(1,0, Button.EXTERIOR));
        e2 = ElevatorSelection.assignBestElevator(e1.popTopRequest(), s1.elevators, s1.numFloors);
        assertNotNull(e2);
        assertEquals(e2.getElevatorID(),3);
    }

    /**
     * Test the getSchedulableScore method
     */
    @Test
    public void testsScheduleScore()
    {
        s2.addToElevators(e2);
        assertEquals(ElevatorSelection.getSchedulableScore(e2),0);
    }

    @Test
    public void testsHardFaultElevator()
    {
    	SchedulerUtils.hardFaultElevator(s4, e4, s4.numFloors);
        assertEquals(ElevatorSelection.getSchedulableScore(e4),0);
    }

    @Test
    public void testEvaluateDirectionalScore()
    {
        assertEquals(ElevatorSelection.evaluateDirectionalScore(e5, new ElevatorRequest(1,2,Button.EXTERIOR), s5.numFloors), 1);
    }

}
