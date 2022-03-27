package protoBufTests;




import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorCommands.FaultMessage;
import elevatorCommands.FaultType;
import elevatorCommands.FloorSensorMessage;
import elevatorCommands.LampMessage;
import elevatorCommands.LampState;
import elevatorCommands.SimulateFaultMessage;
import elevatorCommands.Button;
import elevatorCommands.Direction;
import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRegisterMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.WrapperMessage;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;

class ProtoBufMessageTests{

	
	@BeforeEach
	void init() throws SocketException {
		
	}
	
	@AfterEach
	void after() {
		
	}

	/*
	 * Verify protobuf Wrapper messages being sent and received over udp
	 */
	@Test
	void testSendMessage() throws IOException {
		DatagramSocket sendSock = new DatagramSocket();
		DatagramSocket recvSock = new DatagramSocket(35);
		WrapperMessage testMsg = WrapperMessage.newBuilder().setTEST(true).build();
		DatagramPacket sendPacket = new DatagramPacket(testMsg.toByteArray(), 
														testMsg.toByteArray().length,
														InetAddress.getLocalHost(),
														35);
		sendSock.send(sendPacket);
		byte[] rawReadBytes = new byte[128];
		DatagramPacket recvPack = new DatagramPacket(rawReadBytes, 128);
		recvSock.receive(recvPack);
		byte[] recvArr = Arrays.copyOfRange(recvPack.getData(), 0, testMsg.toByteArray().length);
		assertArrayEquals(testMsg.toByteArray(), recvArr);
		sendSock.close();
		recvSock.close();
	}

	/*
	 * Verify floor can be set and read from FaultMessage
	 */
	@Test 
	void testFaultMessageSetFloor() {
		FaultMessage f = FaultMessage.newBuilder()
				.setFloor(69)
				.build();
		assertEquals(69, f.getFloor());
	}
	
	/*
	 * Verify ElevatorID can be set and read from FaultMessage
	 */	
	@Test 
	void testFaultMessageElevatorID() {
		FaultMessage f = FaultMessage.newBuilder()
				.setElevatorID(69)
				.build();
		assertEquals(69, f.getElevatorID());
	}
	
	/*
	 * Verify Fault Type can be set and read from FaultMessage
	 */
	@Test 
	void testFaultMessageSetFaultType() {
		FaultMessage f1 = FaultMessage.newBuilder()
				.setFault(FaultType.DOORFAULT)
				.build();
		assertEquals(FaultType.DOORFAULT, f1.getFault());
		
		FaultMessage f2 = FaultMessage.newBuilder()
				.setFault(FaultType.ELEVATOR_UNRESPONSIVE)
				.build();
		assertEquals(FaultType.ELEVATOR_UNRESPONSIVE, f2.getFault());

		FaultMessage f3 = FaultMessage.newBuilder()
				.setFault(FaultType.SCHEDULE_FAULT)
				.build();
		assertEquals(FaultType.SCHEDULE_FAULT, f3.getFault());
	}
	
	/*
	 * Verify TimeStamp can be set and read from FaultMessage
	 */
	@Test 
	void testFaultMessageTimeStamp() {
		FaultMessage f = FaultMessage.newBuilder()
				.setTimeStamp("cheese")
				.build();
		assertEquals("cheese", f.getTimeStamp());
	}

	/*
	 * Verify ElevatorID can be set and read from SimFaultMessage
	 */
	@Test 
	void testSimFaultMessageElevatorID() {
		SimulateFaultMessage f = SimulateFaultMessage.newBuilder()
				.setElevatorID(69)
				.build();
		assertEquals(69, f.getElevatorID());
	}
	
	/*
	 * Verify all Fault Types can be set and read from SimFaultMessage
	 */
	@Test 
	void testSimFaultMessageFaultType() {
		SimulateFaultMessage f1 = SimulateFaultMessage.newBuilder()
				.setFault(FaultType.DOORFAULT)
				.build();
		assertEquals(FaultType.DOORFAULT, f1.getFault());
		
		SimulateFaultMessage f2 = SimulateFaultMessage.newBuilder()
				.setFault(FaultType.ELEVATOR_UNRESPONSIVE)
				.build();
		assertEquals(FaultType.ELEVATOR_UNRESPONSIVE, f2.getFault());

		SimulateFaultMessage f3 = SimulateFaultMessage.newBuilder()
				.setFault(FaultType.SCHEDULE_FAULT)
				.build();
		assertEquals(FaultType.SCHEDULE_FAULT, f3.getFault());
	}
	
	/*
	 * Verify Timeout can be set and read from SimFaultMessage
	 */
	@Test 
	void testSimFaultMessageTimeout() {
		SimulateFaultMessage f = SimulateFaultMessage.newBuilder()
				.setTimeout(69)
				.build();
		assertEquals(69, f.getTimeout());
	}
	
	/*
	 * Verify TimeStamp can be set and read from SimFaultMessage
	 */
	@Test 
	void testSimFaultMessageTimeStamp() {
		SimulateFaultMessage f = SimulateFaultMessage.newBuilder()
				.setTimeStamp("cheese")
				.build();
		assertEquals("cheese", f.getTimeStamp());
	}
	
	/*
	 * Verify ElevatorID can be set and read from ElevatorRegisterMessage
	 */
	@Test 
	void testElevatorRegisterMessageElevatorID() {
		ElevatorRegisterMessage f = ElevatorRegisterMessage.newBuilder()
				.setElevatorID(123)
				.build();
		assertEquals(123, f.getElevatorID());
	}
	
	/*
	 * Verify Floor can be set and read from ElevatorRegisterMessage
	 */
	@Test 
	void testElevatorRegisterMessageFloor() {
		ElevatorRegisterMessage f = ElevatorRegisterMessage.newBuilder()
				.setFloor(123)
				.build();
		assertEquals(123, f.getFloor());
	}

	/*
	 * Verify an external button click (required fields) all work
	 */
	@Test 
	void testElevatorRequestMessageExternal() {
		ElevatorRequestMessage f = ElevatorRequestMessage.newBuilder()
				.setFloor(123)
				.setElevatorID(1)
				.setButton(Button.EXTERIOR)
				.setDirection(Direction.UP)
				.setTimeStamp("123")
				.setRequestID(12)
				.build();
		assertEquals(123, f.getFloor());
		assertEquals(1, f.getElevatorID());
		assertEquals(Direction.UP, f.getDirection());
		assertEquals("123", f.getTimeStamp());
		assertEquals(Button.EXTERIOR, f.getButton());
		assertEquals(12, f.getRequestID());
	}
	
	/*
	 * Verify an internal button click (required fields) all work
	 */
	@Test 
	void testElevatorRequestMessageInternal() {
		ElevatorRequestMessage f = ElevatorRequestMessage.newBuilder()
				.setFloor(123)
				.setElevatorID(1)
				.setButton(Button.INTERIOR)
				.setTimeStamp("123")
				.setRequestID(12)
				.build();
		assertEquals(123, f.getFloor());
		assertEquals(1, f.getElevatorID());
		assertEquals(Direction.UP, f.getDirection());
		assertEquals("123", f.getTimeStamp());
		assertEquals(Button.INTERIOR, f.getButton());
		assertEquals(12, f.getRequestID());
	}
	
	/*
	 * Verify Floor can be set and read from ElevatorArrivedMessage
	 */
	@Test 
	void testElevatorArrivedMessageFloor() {
		ElevatorArrivedMessage f = ElevatorArrivedMessage.newBuilder()
				.setFloor(123)
				.build();
		assertEquals(123, f.getFloor());
	}

	/*
	 * Verify TimeStamp can be set and read from ElevatorArrivedMessage
	 */
	@Test 
	void testElevatorArrivedMessageTimeStamp() {
		ElevatorArrivedMessage f = ElevatorArrivedMessage.newBuilder()
				.setTimeStamp("cheese")
				.build();
		assertEquals("cheese", f.getTimeStamp());
	}
	
	/*
	 * Verify ElevatorID can be set and read from ElevatorArrivedMessage
	 */
	@Test 
	void testElevatorArrivedMessageElevatorID() {
		ElevatorArrivedMessage f = ElevatorArrivedMessage.newBuilder()
				.setElevatorID(123)
				.build();
		assertEquals(123, f.getElevatorID());
	}
	
	/*
	 * Verify RequestID can be set and read from FaultMessage
	 */
	@Test 
	void testFaultMessageRequestID() {
		ElevatorArrivedMessage f = ElevatorArrivedMessage.newBuilder()
				.setRequestID(69)
				.build();
		assertEquals(69, f.getRequestID());
	}
	
	/*
	 * Verify Floor can be set and read from ElevatorDepartureMessage
	 */
	@Test 
	void testElevatorDepartureMessageFloor() {
		ElevatorDepartureMessage f = ElevatorDepartureMessage.newBuilder()
				.setInitialFloor(123)
				.build();
		assertEquals(123, f.getInitialFloor());
	}

	/*
	 * Verify TimeStamp can be set and read from ElevatorDepartureMessage
	 */
	@Test 
	void testElevatorDepartureMessageTimeStamp() {
		ElevatorDepartureMessage f = ElevatorDepartureMessage.newBuilder()
				.setTimeStamp("cheese")
				.build();
		assertEquals("cheese", f.getTimeStamp());
	}
	
	/*
	 * Verify ElevatorID can be set and read from ElevatorDepartureMessage
	 */
	@Test 
	void testElevatorDepartureMessageElevatorID() {
		ElevatorDepartureMessage f = ElevatorDepartureMessage.newBuilder()
				.setElevatorID(123)
				.build();
		assertEquals(123, f.getElevatorID());
	}
	
	/*
	 * Verify RequestID can be set and read from ElevatorDepartureMessage
	 */
	@Test 
	void testElevatorDepartureMessageRequestID() {
		ElevatorDepartureMessage f = ElevatorDepartureMessage.newBuilder()
				.setRequestID(69)
				.build();
		assertEquals(69, f.getRequestID());
	}

	/*
	 * Verify Direction can be set and read from ElevatorDepartureMessage
	 */
	@Test 
	void testElevatorDepartureMessageDirection() {
		ElevatorDepartureMessage f1 = ElevatorDepartureMessage.newBuilder()
				.setDirection(Direction.DOWN)
				.build();
		assertEquals(Direction.DOWN, f1.getDirection());
		
		ElevatorDepartureMessage f2 = ElevatorDepartureMessage.newBuilder()
				.setDirection(Direction.UP)
				.build();
		assertEquals(Direction.UP, f2.getDirection());
	}
	
	/*
	 * Verify TimeStamp can be set and read from FloorSensorMessage
	 */
	@Test 
	void testFloorSensorMessageTimeStamp() {
		FloorSensorMessage f = FloorSensorMessage.newBuilder()
				.setTimeStamp("cheese")
				.build();
		assertEquals("cheese", f.getTimeStamp());
	}
	
	/*
	 * Verify Floor can be set and read from FloorSensorMessage
	 */
	@Test 
	void testFloorSensorMessageFloor() {
		FloorSensorMessage f = FloorSensorMessage.newBuilder()
				.setFloor(123)
				.build();
		assertEquals(123, f.getFloor());
	}
	
	/*
	 * Verify Direction can be set and read from FloorSensorMessage
	 */
	@Test 
	void testLampMessageDirection() {
		LampMessage f1 = LampMessage.newBuilder()
				.setDirection(Direction.DOWN)
				.build();
		assertEquals(Direction.DOWN, f1.getDirection());
		
		LampMessage f2 = LampMessage.newBuilder()
				.setDirection(Direction.UP)
				.build();
		assertEquals(Direction.UP, f2.getDirection());
	}	
	
	/*
	 * Verify LampState can be set and read from LampMessage
	 */
	@Test 
	void testLampMessageState() {
		LampMessage f1 = LampMessage.newBuilder()
				.setState(LampState.OFF)
				.build();
		assertEquals(LampState.OFF, f1.getState());
	
		LampMessage f2 = LampMessage.newBuilder()
				.setState(LampState.ON)
				.build();
		assertEquals(LampState.ON, f2.getState());
	}
	
	/*
	 * Verify Floor can be set and read from LampMessage
	 */
	@Test 
	void testLampMessageFloor() {
		LampMessage f = LampMessage.newBuilder()
				.setFloor(123)
				.build();
		assertEquals(123, f.getFloor());
	}
	
	/*
	 * Verify ElevatorID can be set and read from LampMessage
	 */
	@Test 
	void testLampMessageElevatorId() {
		LampMessage f = LampMessage.newBuilder()
				.setElevatorID(69)
				.build();
		assertEquals(69, f.getElevatorID());
	}
}
