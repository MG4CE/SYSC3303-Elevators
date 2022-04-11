package scheduler;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import org.junit.jupiter.api.*;

import communication.ProtoBufMessage;
import communication.UDPHelper;
import message.Button;
import message.Direction;
import message.ElevatorArrivedMessage;
import message.ElevatorDepartureMessage;
import message.ElevatorRegisterMessage;
import message.ElevatorRequestMessage;
import message.FloorSensorMessage;
import message.WrapperMessage;
import scheduler.ElevatorControl.ElevatorState;


/**
 * Testing the Scheduler
 * @Golan
 */
public class SchedulerTests {
    static ElevatorControl e1, e2, e3 ,e4 ,e5, e6;
    static Scheduler s1, s2, s3, s4, s5, s6;

    @BeforeAll
    public static void init() throws SocketException {
        s1 = new Scheduler(3000,5);
        s2 = new Scheduler(3001,5);
        s3 = new Scheduler(3002,5);
        s4 = new Scheduler(3003,5);
        s5 = new Scheduler(3004,5);
        s6 = new Scheduler(3005,5);
        e1 = new ElevatorControl(3000,3,0,null,s1);
        e2 = new ElevatorControl(3001,3,0,null,s2);
        e3 = new ElevatorControl(3002,3,0,null,s3);
        e4 = new ElevatorControl(3003,3,0,null,s4);
        e5 = new ElevatorControl(3004,3,0,null,s5);
        e6 = new ElevatorControl(3005,3,0,null,s6);
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

    /**
     * Test the assignBestElevatorClass
     */
    @Test
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
    
    /**
     * Test hardFaultElevator method 
     */
    @Test
    public void testsHardFaultElevator()
    {
    	SchedulerUtils.hardFaultElevator(s4, e4, s4.numFloors);
        assertEquals(ElevatorSelection.getSchedulableScore(e4),0);
    }

    /**
     * Test evaluateDirectionalScore method
     */
    @Test
    public void testEvaluateDirectionalScore()
    {
        assertEquals(ElevatorSelection.evaluateDirectionalScore(e5, new ElevatorRequest(1,2,Button.EXTERIOR), s5.numFloors), 1);
    }
    
    /**
     * Test HandleElevatorRequestMessage
     * 
     * TODO: add data socket receive check in all
     */
    @Test
    public void testHandleElevatorRequestMessage() {
    	Scheduler s = null;
    	try {
			s = new Scheduler(3303, 5);
		} catch (SocketException e) {
			e.printStackTrace();
			fail();
		}
    	
    	ElevatorControl e = new ElevatorControl(3303, 3, 0, InetAddress.getLoopbackAddress(), s);
    	s.addToElevators(e);
    	
    	ElevatorRequestMessage r = ElevatorRequestMessage.newBuilder()
                .setFloor(1)
                .setButton(Button.EXTERIOR)
                .setDirection(Direction.UP)
                .setRequestID(1)
                .build();
    	
    	WrapperMessage wmsg = null;
		try {
			wmsg = UDPHelper.createWrapperMessage(r);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail();
		}
    	
    	DatagramPacket p = new DatagramPacket(r.toByteArray(), r.toByteArray().length);
    	p.setPort(0);
    	p.setAddress(null);
    	
    	ProtoBufMessage msg = new ProtoBufMessage(wmsg);
		
    	MessageHandlers.handleElevatorRequestMessage(s, p, msg);
    	
    	assertEquals(s.floorSubsystemAddress, null);
    	assertEquals(s.floorSubsystemPort, 0);
    	assertEquals(e.peekTopRequest().getDirection(), Direction.UP);
    	assertEquals(e.peekTopRequest().getFloor(), 1);
    	assertEquals(e.peekTopRequest().getRequestID(), 1);
    	assertEquals(e.peekTopRequest().getRequestType(), Button.EXTERIOR);
    	assertFalse(e.isTimeoutTimerOff());
    	e.stopTimeoutTimer();
    }
    
    /**
     * Test HandleElevatorRegisterMessage
     * 
     * TODO: add data socket receive check in all
     */
    @Test
    public void testHandleElevatorRegisterMessage() {
       	Scheduler s = null;
    	try {
			s = new Scheduler(3304, 5);
		} catch (SocketException e) {
			e.printStackTrace();
			fail();
		}
    	
    	ElevatorRegisterMessage r = ElevatorRegisterMessage.newBuilder()
    			.setFloor(1)
                .build();
    	
    	WrapperMessage wmsg = null;
		try {
			wmsg = UDPHelper.createWrapperMessage(r);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail();
		}
    	
    	DatagramPacket p = new DatagramPacket(r.toByteArray(), r.toByteArray().length);
    	p.setPort(999);
    	p.setAddress(InetAddress.getLoopbackAddress());
    	
    	ProtoBufMessage msg = new ProtoBufMessage(wmsg);
		
    	MessageHandlers.handleElevatorRegisterMessage(s, p, msg);
    	
    	assertEquals(s.getElevatorControl().get(0).getAddress(), InetAddress.getLoopbackAddress());
    	assertEquals(s.getElevatorControl().get(0).getPort(), 999);
    	assertEquals(s.getElevatorControl().get(0).getCurrentFloor(), 1);
    }
    
    /**
     * Test HandleElevatorArrivedMessage
     * 
     * TODO: add data socket receive check in all
     */
    @Test
    public void testHandleElevatorArrivedMessage() {
    	Scheduler s = null;
    	try {
			s = new Scheduler(3305, 5);
		} catch (SocketException e) {
			e.printStackTrace();
			fail();
		}
    	
    	ElevatorControl e = new ElevatorControl(3303, 3, 0, InetAddress.getLoopbackAddress(), s);
    	e.addDestination(new ElevatorRequest(3, 1, Button.EXTERIOR));
    	s.addToElevators(e);
    	
    	ElevatorArrivedMessage r = ElevatorArrivedMessage.newBuilder()
                .setFloor(3)
                .setElevatorID(3)
                .setRequestID(1)
                .build();
    	
    	WrapperMessage wmsg = null;
		try {
			wmsg = UDPHelper.createWrapperMessage(r);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail();
		}
    	
    	DatagramPacket p = new DatagramPacket(r.toByteArray(), r.toByteArray().length);
    	p.setPort(0);
    	p.setAddress(null);
    	
    	ProtoBufMessage msg = new ProtoBufMessage(wmsg);
		
    	s.floorSubsystemAddress = InetAddress.getLoopbackAddress();
    	s.floorSubsystemPort = 9999;
    	
    	MessageHandlers.handleElevatorArrivedMessage(s, p, msg);
    	
    	assertEquals(e.peekTopRequest(), null);
    	//this case is not covered floor sensor handles this
    	//assertEquals(e.getCurrentFloor(), 3);
    	assertEquals(e.getState(), ElevatorState.STOPPED);
    	assertFalse(e.isTimeoutTimerOff());
    	e.stopTimeoutTimer();
    }
    
    
    /**
     * Test HandleElevatorDepartureMessage
     * 
     * TODO: add data socket receive check in all
     */
    @Test
    public void testHandleElevatorDepartureMessage() {
    	Scheduler s = null;
    	try {
			s = new Scheduler(3306, 5);
		} catch (SocketException e) {
			e.printStackTrace();
			fail();
		}
    	
    	ElevatorControl e = new ElevatorControl(3303, 3, 0, InetAddress.getLoopbackAddress(), s);
    	s.addToElevators(e);
    	
    	ElevatorDepartureMessage r = ElevatorDepartureMessage.newBuilder()
                .setDirection(Direction.UP)
                .setRequestID(0)
                .setElevatorID(3)
                .build();
    	
    	WrapperMessage wmsg = null;
		try {
			wmsg = UDPHelper.createWrapperMessage(r);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail();
		}
    	
    	DatagramPacket p = new DatagramPacket(r.toByteArray(), r.toByteArray().length);
    	p.setPort(0);
    	p.setAddress(null);
    	
    	ProtoBufMessage msg = new ProtoBufMessage(wmsg);
		
    	s.floorSubsystemAddress = InetAddress.getLoopbackAddress();
    	s.floorSubsystemPort = 9999;
    	
    	MessageHandlers.handleElevatorDepartureMessage(s, p, msg);
    	
    	assertEquals(e.getState(), ElevatorState.MOVING);
    	assertFalse(e.isTimeoutTimerOff());
    	e.stopTimeoutTimer();
    }
    
    /**
     * Test HandleFloorSensorMessage
     */
    @Test
    public void testHandleFloorSensorMessage() {
    	Scheduler s = null;
    	try {
			s = new Scheduler(3307, 5);
		} catch (SocketException e) {
			e.printStackTrace();
			fail();
		}
    	
    	ElevatorControl e = new ElevatorControl(3303, 3, 0, InetAddress.getLoopbackAddress(), s);
    	s.addToElevators(e);
    	
    	FloorSensorMessage r = FloorSensorMessage.newBuilder()
                .setElevatorID(3)
                .setFloor(2)
                .build();
    	
    	WrapperMessage wmsg = null;
		try {
			wmsg = UDPHelper.createWrapperMessage(r);
		} catch (IOException e2) {
			e2.printStackTrace();
			fail();
		}
    	
    	DatagramPacket p = new DatagramPacket(r.toByteArray(), r.toByteArray().length);
    	p.setPort(0);
    	p.setAddress(null);
    	
    	ProtoBufMessage msg = new ProtoBufMessage(wmsg);
    	
    	MessageHandlers.handleFloorSensorMessage(s, p, msg);
    	
    	assertEquals(e.getCurrentFloor(), 2);
    	assertFalse(e.isTimeoutTimerOff());
    	e.stopTimeoutTimer();
    }
}
