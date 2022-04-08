package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

import communication.ProtoBufMessage;
import message.Button;
import message.ElevatorArrivedMessage;
import message.ElevatorDepartureMessage;
import message.ElevatorRegisterMessage;
import message.ElevatorRequestMessage;
import message.FaultMessage;
import message.FaultType;
import message.FloorSensorMessage;
import message.SimulateFaultMessage;
import scheduler.ElevatorControl.ElevatorState;

public class MessageHandlers {
	
	/**
	 * Handles incoming ElevatorRequestMessage's, EXTERIOR button requests will get directly scheduled to
	 * an elevator if possible and dispatched if the request is top priority. INTERIOR button requests get
	 * scheduled to the elevator it belongs to and dispatched if the request is top priority.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of ElevatorRequestMessage
	 */
	protected static void handleElevatorRequestMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		ElevatorRequestMessage request = msg.toElevatorRequestMessage();
		
		if (s.floorSubsystemPort == -1 || s.floorSubsystemAddress == null) {
			s.floorSubsystemPort = packet.getPort();
			s.floorSubsystemAddress = packet.getAddress();
		}
		
		if(request.getButton().equals(Button.EXTERIOR)) {
			Scheduler.LOGGER.info("Exterior button pressed at floor " + request.getFloor() + " direction " + request.getDirection());
			ElevatorRequest eReq = new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection(), Button.EXTERIOR);
			ElevatorControl elevator = ElevatorSelection.assignBestElevator(eReq, s.elevators, s.numFloors);
			if(elevator == null) {
				Scheduler.LOGGER.info("No elevators avaliable, ignoring request!");
				return;
			}
			elevator.increaseSameFloorPriority();
			synchronized(elevator){
				if(elevator.peekTopRequest().getFloor() == request.getFloor() && elevator.peekTopRequest().getFloor() != elevator.getCurrentDestination()) {
					if(elevator.isSchedulable()) {
						elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
						if(elevator.isTimeoutTimerOff()) {
							elevator.startTimeoutTimer();
						}
						try {
							SchedulerMessages.sendSchedulerDispatchMessage(s, elevator.peekTopRequest().getFloor(), elevator.getPort(), request.getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			Scheduler.LOGGER.info("Interior button pressed inside Elevator " + request.getElevatorID() + " requesting to go to floor " + request.getFloor());
			for(ElevatorControl elevator : s.elevators) {
				if(request.getElevatorID() == elevator.getElevatorID()) {
					synchronized(elevator){
						elevator.stopWaitTimer();
						elevator.addDestination(new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection(), Button.INTERIOR));
						if(elevator.isSchedulable()) {
							elevator.increaseSameFloorPriority();
							elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
							try {
								SchedulerMessages.sendSchedulerDispatchMessage(s, elevator.peekTopRequest().getFloor(), elevator.getPort(), request.getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}	
					}
				}
			}
		}
	}
	
	/**
	 * Handles incoming ElevatorRegisterMessage's, which creates an elevator control object for the invoking elevator
	 * and hands/assigns the elevator an ID.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of ElevatorRegisterMessage
	 */
	protected static void handleElevatorRegisterMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		ElevatorRegisterMessage message = msg.toElevatorRegisterMessage();
		s.elevators.add(new ElevatorControl(packet.getPort(), s.elevatorIDCounter, message.getFloor(), packet.getAddress(), s));
		try {
			SchedulerMessages.sendElevatorRegisterMessage(s, s.elevatorIDCounter, packet.getPort(), packet.getAddress());
		} catch (IOException e) {
			Scheduler.LOGGER.error("Failed to send repsonse to elevator register message: " + e.getMessage());
			s.stopScheduler();
		}
		Scheduler.LOGGER.info("Registered an new elevator with ID " + s.elevatorIDCounter);
		s.elevatorIDCounter++;
	}
	
	/**
	 * Handles incoming ElevatorArrivedMessage's, on arrival it sets the elevator control state to stopped and tries
	 * to dispatch the elevator to the next destination, also forwards the message to the floor subsystem. If the 
	 * request being serviced is an external button request only start a timer that wait for internal button request to arrive.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of ElevatorArrivedMessage
	 */
	protected static void handleElevatorArrivedMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		ElevatorArrivedMessage message = msg.toElevatorArrivedMessage();
		Scheduler.LOGGER.info("Elevator " + message.getElevatorID() + " has arrived at floor " + message.getFloor());
		for(ElevatorControl elevator : s.elevators) {
			synchronized(elevator){
				if(message.getElevatorID() == elevator.getElevatorID()) {
					elevator.resetTimeoutTimer();
					try {
						SchedulerMessages.sendElevatorArrivedMessage(s, message, elevator.peekTopRequest().getRequestID(), s.floorSubsystemPort, s.floorSubsystemAddress);
					} catch (IOException e) {
						Scheduler.LOGGER.error("Failed to forward elevator arrived message: " + e.getMessage());
					}
					elevator.setState(ElevatorState.STOPPED);
					ElevatorRequest lastReq = null;
					if(elevator.getCurrentDestination() == elevator.peekTopRequest().getFloor()) {
						lastReq = elevator.popTopRequest();
					} else {
						//This not a great way of removing a request if we reschedule an elevator in the event
						//of an elevator request redistribution due to a hard fault
						//this is bad
						ArrayList<ElevatorRequest> requests = elevator.getFloorDestinations();
						for(int i = 0; i < elevator.getNumDestinations(); i++) {
							if(message.getFloor() == requests.get(i).getFloor()) {
								lastReq = requests.remove(i);
							}
						}
					}
					
					if(lastReq == null) {
						throw new IllegalStateException("Arrived floor not found queue, how did we get here?");
					}
					
					elevator.increaseSameFloorPriority();
					if (!elevator.getFloorDestinations().isEmpty() && lastReq.getRequestType() == Button.INTERIOR) {
						elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
						if(elevator.isSchedulable()) {
							try {
								SchedulerMessages.sendSchedulerDispatchMessage(s, elevator.peekTopRequest().getFloor(), elevator.getPort(), elevator.getlDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else if (lastReq.getRequestType() == Button.EXTERIOR) {
						elevator.startWaitTimer();
					} else {
						elevator.stopTimeoutTimer();
					}
				}
			}
		}
	}
	
	/**
	 * Handles incoming ElevatorDepartureMessage's, which updates the elevator control object state to MOVING and 
	 * forwards the message to the floor subsystem.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of ElevatorDepartureMessage
	 */
	protected static void handleElevatorDepartureMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		ElevatorDepartureMessage message = msg.toElevatorDepartureMessage();
		Scheduler.LOGGER.debug("Elevator " + message.getElevatorID() + " is now moving");
		try {
			s.sendMessage(message, s.floorSubsystemPort, s.floorSubsystemAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(ElevatorControl elevator : s.elevators) {
			if(message.getElevatorID() == elevator.getElevatorID()) {
				synchronized(elevator){
					elevator.resetTimeoutTimer();
					elevator.setState(ElevatorState.MOVING);
				}
			}
		}
	}
	
	/**
	 * Handles incoming FloorSensorMessage's, which updates the current floor of the elevator control object.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of FloorSensorMessage
	 */
	protected static void handleFloorSensorMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		FloorSensorMessage request = msg.toFloorSensorMessage();
		Scheduler.LOGGER.debug("Elevator " + request.getElevatorID() + " is currently at floor " + request.getFloor());
		for(ElevatorControl elevator : s.elevators) {
			if(request.getElevatorID() == elevator.getElevatorID()) {
				synchronized(elevator){
					elevator.resetTimeoutTimer();
					elevator.setCurrentFloor(request.getFloor());
				}
			}
		}
	}
	
	/**
	 * Handles incoming ElevatorSimulateFaultMessage's, which makes preparation for the specific simulation fault and
	 * forwards the message to the correct target elevator.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of ElevatorSimulateFaultMessage
	 */
	protected static void handleElevatorSimulateFaultMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		SimulateFaultMessage request = msg.toElevatorSimulateFaultMessage();
		Scheduler.LOGGER.info("Simulating " + request.getFault() + " for Elevator " + request.getElevatorID() + " duration " + request.getTimeout() + "millis");
		for(ElevatorControl elevator : s.elevators) {
			if(request.getElevatorID() == elevator.getElevatorID()) {
				if(request.getFault() == FaultType.DOORFAULT) {
					elevator.createDoorFaultSimTimer(request.getTimeout());
				}
				try {
					s.sendMessage(request, elevator.getPort(), elevator.getAddress());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(request.getFault() == FaultType.ELEVATOR_UNRESPONSIVE) {
					elevator.resetTimeoutTimer();
				}
			}
		}
	}
	
	/**
	 * Handles incoming FaultMessage's. For door faults we update the state of the elevator control object and start the
	 * simulated door fault timer, that ends the fault. For schedule faults the top request is popped and rescheduled.
	 * When a door fault is resolved the state is updated to the stopped state and the current destination is verified.
	 * 
	 * @param s Instance of scheduler for control
	 * @param packet Raw packet, required for address and port information
	 * @param msg ProtoBufMessage that is an instance of FaultMessage
	 */
	protected static void handleElevatorFaultMessage(Scheduler s, DatagramPacket packet, ProtoBufMessage msg) {
		FaultMessage request = msg.toElevatorFaultMessage();
		ElevatorControl elevatorAtFault = null;
		for(ElevatorControl elevator : s.elevators) {
			if(request.getElevatorID() == elevator.getElevatorID()) {
				elevatorAtFault = elevator;
			}
		}
		
		if(elevatorAtFault != null) {
			if(request.getFault() == FaultType.DOORFAULT) {
				Scheduler.LOGGER.info("Elevator " + request.getElevatorID() + " has encountred a door fault");
				elevatorAtFault.stopTimeoutTimer();
				elevatorAtFault.setState(ElevatorState.DOOR_FAULT);
				elevatorAtFault.startDoorFaultSimTimer();
			} else if(request.getFault() == FaultType.SCHEDULE_FAULT) {
				Scheduler.LOGGER.info("Elevator " + request.getElevatorID() + " has encountred scheduling fault");
				elevatorAtFault.resetTimeoutTimer();
				ElevatorRequest r = elevatorAtFault.popTopRequest();
				elevatorAtFault.addDestination(r);
			} else if (request.getFault() == FaultType.RESOLVED) {
				Scheduler.LOGGER.info("Elevator " + request.getElevatorID() + " has resolved door fault");
				elevatorAtFault.resetTimeoutTimer();
				elevatorAtFault.setState(ElevatorState.STOPPED);
				SchedulerUtils.verifyElevatorTopRequests(s);
			}
		}
	}
}
