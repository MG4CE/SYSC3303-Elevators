package scheduler;

import java.io.IOException;
import java.net.InetAddress;

import message.Direction;
import message.ElevatorArrivedMessage;
import message.ElevatorRegisterMessage;
import message.FaultType;
import message.LampMessage;
import message.SchedulerDispatchMessage;
import message.SimulateFaultMessage;

public class SchedulerMessages {
	
	/**
	 * If we have an elevator departing then we need to set the lamp
	 * 
	 * @param s Scheduler
	 * @param floor the floor the lamp is related to
	 * @param port the port
	 * @param direction the direction of the lamp messages
	 * @param elevatorID the id of the elevator related to the lamp
	 * @param address the InetAddress
	 * @throws IOException an exception for UDP
	 */
	protected static void sendLampMessage(Scheduler s, int floor, int port, Direction direction, int elevatorID, InetAddress address) throws IOException {
		LampMessage lampMsg = LampMessage.newBuilder()
				.setFloor(floor)
				.setElevatorID(elevatorID)
				.setDirection(direction)
				//TODO: ADD TIMESTAMP
				.build();

		s.sendMessage(lampMsg, port, address);
	}

	/**
	 * When an elevator arrives send another dispatch message, and another message is in queue.
	 * If the button pressed has the highest priority send a dispatch
	 * 
	 * @param s Scheduler
	 * @param destFloor the destination floor
	 * @param port the port to send to
	 * @param direction the direction of the elevator
	 * @param elevatorID the elevator to which to send the command
	 * @param address The InetAddress
	 * @throws IOException an exception with UDP
	 */
	protected static void sendSchedulerDispatchMessage(Scheduler s, int destFloor, int port, Direction direction, int requestID, int elevatorID, InetAddress address) throws IOException {
		Scheduler.LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + " to floor " + Integer.toString(destFloor));
		SchedulerDispatchMessage dispatchMsg = SchedulerDispatchMessage.newBuilder()
				.setDestFloor(destFloor)
				.setElevatorID(elevatorID)
				.setRequestID(requestID)
				//TODO: ADD TIMESTAMP
				.build();

		s.sendMessage(dispatchMsg, port, address);
	}
	
	/**
	 * Send a UDP message indicating elevator has arrived at a floor
	 * 
	 * @param s
	 * @param message ElevatorArrivedMessage message to be updated
	 * @param requestID requestID of serviced request that arrived
	 * @param port FloorSubsystem port
	 * @param address FloorSubsystem InetAddress
	 * @throws IOException an exception with UDP
	 */
	protected static void sendElevatorArrivedMessage(Scheduler s, ElevatorArrivedMessage message, int requestID, int port, InetAddress address) throws IOException {
 		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder()
				.setElevatorID(message.getElevatorID())
				.setFloor(message.getFloor())
				.setRequestID(requestID)
				//TODO: ADD TIMESTAMP
				.build();
		s.sendMessage(msg, port, address);
	}
	
	/**
	 * Send a UDP message to make the elevator stop the simulated door fault
	 * 
	 * @param s Scheduler
	 * @param elevatorID the id of the registered elevator
	 * @param port the port of the elevator
	 * @param address the InetAddress
	 * @throws IOException an exception with UDP
	 */
	protected static void sendStopDoorFaultSimulateFaultMessage(Scheduler s, int elevatorID, int port, InetAddress address) throws IOException {
		SimulateFaultMessage msg = SimulateFaultMessage.newBuilder()
				.setFault(FaultType.DOORFAULT)
				.setElevatorID(elevatorID)
				.setTimeout(0)
				//TODO: ADD TIMESTAMP
				.build();
		s.sendMessage(msg, port, address);
	}
	
	/**
	 * Send a message saying a that an elevator has been registered
	 * 
	 * @param s Scheduler
	 * @param elevatorID the id of the registered elevator
	 * @param port the port of the elevator
	 * @param address the InetAddress
	 * @throws IOException an exception with UDP
	 */
	protected static void sendElevatorRegisterMessage(Scheduler s, int elevatorID, int port, InetAddress address) throws IOException {
		ElevatorRegisterMessage msg = ElevatorRegisterMessage.newBuilder()
				.setElevatorID(elevatorID)
				//TODO: ADD TIMESTAMP
				.build();

		s.sendMessage(msg, port, address);
	}
}
