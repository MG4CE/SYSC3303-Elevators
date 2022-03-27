package scheduler;

import elevatorCommands.*;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;
import scheduler.Elevator.ElevatorState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * The Scheduler class will be running one of the main threads in the system.
 * It will be responsible for facilitating the sending of messages to and from the elevator
 * and the floor subsystem
 */
public class Scheduler extends UDPHelper {
	//A logger for all the message logs
    protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Scheduler.class);  
	//The number of floors
    private int numFloors;
	//A queue of the messages
    private ArrayList<DatagramPacket> messageQueue;
	//The elevator control list which represents the changes to the actual elevators
    private ArrayList<Elevator> elevators;
	//The counter for the elevator ids
    private int elevatorIDCounter;
	//the threads that will be running
    private Thread schedulerThread, listenerThread;
	//Make sure the threads are running
    private Boolean isRunning;
	//The floor subsystem port
    private int floorSubsystemPort;
	//The InetAddress for the floor subsystem
    private InetAddress floorSubsystemAddress;
    
	/**
	 * The constructor for the Scheduler
	 * @param listenPort the port to listen to
	 * @param numFloors the number of floors
	 * @throws SocketException an exception with UDP
	 */
    public Scheduler(int listenPort, int numFloors) throws SocketException {
        super(listenPort);
        this.numFloors = numFloors;
        this.messageQueue = new ArrayList<>();
        this.elevators = new ArrayList<>();
        this.elevatorIDCounter = 1;
        this.isRunning = true;
        this.floorSubsystemPort = -1;
        this.floorSubsystemAddress = null;
    }

	/**
	 * The thread responsible for handling all incoming messages
	 * and adding the to a queue of messages
	 */
	public void startListenerThread() {
		listenerThread = new Thread(new Runnable() {
			public void run() {
				while (isRunning) {
					DatagramPacket msg = null;
					try {
						msg = receiveMessage();
					} catch (IOException e1) {
						e1.printStackTrace();
						LOGGER.error("Failed to receive data from socket, stopping!");
						isRunning = false;
						schedulerThread.interrupt();
					}
					
					if(!listenerThread.isInterrupted() && msg != null) {
						synchronized (messageQueue) {
							//Add the messages and notify that a message was added
							messageQueue.add(msg);
							messageQueue.notifyAll();
						}
					}

				}
			}
		});
		listenerThread.start();
	}

	/**
	 * The thread responsible for taking in all types of appropriate messages in the queue and dealing
	 * with them accordingly.
	 * 
	 * TODO: add IdleMessage to system and handle it
	 */
	public void startSchedulingThread() {
		Scheduler s = this;
    	schedulerThread = new Thread(new Runnable() {
    		public void run() {
    			while(isRunning) {
	    			synchronized(messageQueue) {
	    				while(messageQueue.isEmpty()) {
							try {
								messageQueue.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
	    				
	    				if(schedulerThread.isInterrupted() || !isRunning) {
	    					break;
	    				}
	    				
	    				DatagramPacket packet = messageQueue.remove(0);
	    				
	    				ProtoBufMessage msg = null;
						try {
							msg = new ProtoBufMessage(packet);
						} catch (InvalidProtocolBufferException e) {
							LOGGER.error("Failed to convert received packet into protobuf message, skipping: " + e.getMessage());
							continue;
						}
	    				
	    				if(msg.isElevatorRequestMessage()) {
	    					ElevatorRequestMessage request = msg.toElevatorRequestMessage();
	    					
	    					if (floorSubsystemPort == -1 || floorSubsystemAddress == null) {
	    						floorSubsystemPort = packet.getPort();
	    						floorSubsystemAddress = packet.getAddress();
	    					}
	    					
	    					if(request.getButton().equals(Button.EXTERIOR)) {
	    						LOGGER.info("Exterior button pressed at floor " + request.getFloor() + " direction " + request.getDirection());
	    						ElevatorRequest eReq = new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection(), Button.EXTERIOR);
	    						Elevator elevator = assignBestElevator(eReq);
	    						if(elevator == null) {
	    							LOGGER.info("No elevators avaliable, ignoring request!");
	    							continue;
	    						}
	    						elevator.increaseSameFloorPriority();
	    						synchronized(elevator){
		    						if(elevator.peekTopRequest().getFloor() == request.getFloor() && elevator.peekTopRequest().getFloor() != elevator.getCurrentDestination()) {
		    							System.out.println(elevator.peekTopRequest().getFloor() + " " + request.getFloor() + " " + elevator.peekTopRequest().getFloor() + " " + elevator.getCurrentDestination());
		    							elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
										if(elevator.isTimeoutTimerOff()) {
											System.out.println("Elevator " + elevator.getElevatorID() + " started timeout timer");
											elevator.startTimeoutTimer();
										}
		    							if(elevator.isSchedulable()) {
											try {
												sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), request.getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
											} catch (IOException e) {
												e.printStackTrace();
											}
		    							}
									}
	    						}
	    					} else {
	    						LOGGER.info("Interior button pressed inside Elevator " + request.getElevatorID() + " requesting to go to floor " + request.getFloor());
								for(Elevator elevator : elevators) {
									if(request.getElevatorID() == elevator.getElevatorID()) {
										synchronized(elevator){
											elevator.stopWaitTimer();
											elevator.addDestination(new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection(), Button.INTERIOR));
											elevator.increaseSameFloorPriority();
											elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
											if(elevator.isSchedulable()) {
												try {
													sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), request.getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
												} catch (IOException e) {
													e.printStackTrace();
												}
											}	
										}
									}
								}
							}
	    				} else if(msg.isElevatorRegisterMessage()) {
							ElevatorRegisterMessage message = msg.toElevatorRegisterMessage();
	    					elevators.add(new Elevator(packet.getPort(), elevatorIDCounter, message.getFloor(), packet.getAddress(), s));
	    					try {
								sendElevatorRegisterMessage(elevatorIDCounter, packet.getPort(), packet.getAddress());
							} catch (IOException e) {
								LOGGER.error("Failed to send repsonse to elevator register message: " + e.getMessage());
								stopScheduler();
							}
	    					LOGGER.info("Registered an new elevator with ID " + elevatorIDCounter);
	    					elevatorIDCounter++;
	    				} else if(msg.isElevatorArrivedMessage()) {
							ElevatorArrivedMessage message = msg.toElevatorArrivedMessage();
	    					LOGGER.info("Elevator " + message.getElevatorID() + " has arrived at floor " + message.getFloor());
							for(Elevator elevator : elevators) {
								synchronized(elevator){
									if(message.getElevatorID() == elevator.getElevatorID()) {
										elevator.resetTimeoutTimer();
										try {
											sendElevatorArrivedMessage(message, elevator.peekTopRequest().getRequestID(), floorSubsystemPort, floorSubsystemAddress);
										} catch (IOException e) {
											LOGGER.error("Failed to forward elevator arrived message: " + e.getMessage());
										}
										elevator.setState(ElevatorState.STOPPED);
										ElevatorRequest lastReq = elevator.popTopRequest();
										elevator.increaseSameFloorPriority();
										if (!elevator.getFloorDestinations().isEmpty() && lastReq.getRequestType() == Button.INTERIOR) {
											elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
											if(elevator.isSchedulable()) {
												try {
													sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), elevator.getlDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										} else if (lastReq.getRequestType() == Button.EXTERIOR) {
											elevator.startWaitTimer();
										} else {
											System.out.println("Elevator " + elevator.getElevatorID() + " stopped timout timer");
											elevator.stopTimeoutTimer();
										}
									}
								}
							}
						} else if(msg.isElevatorDepartureMessage()) {
							ElevatorDepartureMessage message = msg.toElevatorDepartureMessage();
							LOGGER.info("Elevator " + message.getElevatorID() + " is now moving");
							try {
								sendMessage(message, floorSubsystemPort, floorSubsystemAddress);
							} catch (IOException e) {
								e.printStackTrace();
							}
							for(Elevator elevator : elevators) {
								if(message.getElevatorID() == elevator.getElevatorID()) {
									synchronized(elevator){
										System.out.println("2 Elevator " + elevator.getElevatorID() + " timer reset");
										elevator.resetTimeoutTimer();
										elevator.setState(ElevatorState.MOVING);
									}
								}
							}
						} else if(msg.isFloorSensorMessage()) {
							FloorSensorMessage request = msg.toFloorSensorMessage();
							LOGGER.debug("Elevator " + request.getElevatorID() + " is currently at floor " + request.getFloor());
							for(Elevator elevator : elevators) {
								if(request.getElevatorID() == elevator.getElevatorID()) {
									synchronized(elevator){
										System.out.println("3 Elevator " + elevator.getElevatorID() + " timer reset");
										elevator.resetTimeoutTimer();
										elevator.setCurrentFloor(request.getFloor());
									}
								}
							}
						} else if(msg.isElevatorSimulateFaultMessage()) {
							SimulateFaultMessage request = msg.toElevatorSimulateFaultMessage();
							LOGGER.info("Simulating " + request.getFault() + " for Elevator " + request.getElevatorID() + " duration " + request.getTimeout() + "millis");
							for(Elevator elevator : elevators) {
								if(request.getElevatorID() == elevator.getElevatorID()) {
									if(request.getFault() == FaultType.DOORFAULT) {
										elevator.createDoorFaultSimTimer(request.getTimeout());
									}
									try {
										sendMessage(request, elevator.getPort(), elevator.getAddress());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						} else if(msg.isElevatorFaultMessage()) {
							FaultMessage request = msg.toElevatorFaultMessage();
							Elevator elevatorAtFault = null;
							for(Elevator elevator : elevators) {
								if(request.getElevatorID() == elevator.getElevatorID()) {
									elevatorAtFault = elevator;
								}
							}
							
							if(elevatorAtFault != null) {
								if(request.getFault() == FaultType.DOORFAULT) {
									LOGGER.info("Elevator " + request.getElevatorID() + " has encountred a door fault");
									elevatorAtFault.stopTimeoutTimer();
									elevatorAtFault.setState(ElevatorState.DOOR_FAULT);
									elevatorAtFault.startDoorFaultSimTimer();
								} else if(request.getFault() == FaultType.SCHEDULE_FAULT) {
									LOGGER.info("Elevator " + request.getElevatorID() + " has encountred scheduling fault");
									System.out.println("4 Elevator " + elevatorAtFault.getElevatorID() + " timer reset");
									elevatorAtFault.resetTimeoutTimer();
									ElevatorRequest r = elevatorAtFault.popTopRequest();
									elevatorAtFault.addDestination(r);
								} else if (request.getFault() == FaultType.RESOLVED) {
									LOGGER.info("Elevator " + request.getElevatorID() + " has resolved door fault");
									elevatorAtFault.setState(ElevatorState.STOPPED);
									verifyElevatorTopRequests();
								}
							}
						}
					}	
	    		}
    		}
    	});
		schedulerThread.start();
	}

	/**
	 * If we have an elevator departing then we need to set the lamp
	 * 
	 * @param floor the floor the lamp is related to
	 * @param port the port
	 * @param direction the direction of the lamp messages
	 * @param elevatorID the id of the elevator related to the lamp
	 * @param address the InetAddress
	 * @throws IOException an exception for UDP
	 */
	@SuppressWarnings("unused")
	private void sendLampMessage(int floor, int port, Direction direction, int elevatorID, InetAddress address) throws IOException {
		LampMessage lampMsg = LampMessage.newBuilder()
				.setFloor(floor)
				.setElevatorID(elevatorID)
				.setDirection(direction)
				.build();

		sendMessage(lampMsg, port, address);
	}

	/**
	 * When an elevator arrives send another dispatch message, and another message is in queue.
	 * If the button pressed has the highest priority send a dispatch
	 * 
	 * @param destFloor the destination floor
	 * @param port the port to send to
	 * @param direction the direction of the elevator
	 * @param elevatorID the elevator to which to send the command
	 * @param address The InetAddress
	 * @throws IOException an exception with UDP
	 */
	protected void sendSchedulerDispatchMessage(int destFloor, int port, Direction direction, int requestID, int elevatorID, InetAddress address) throws IOException {
		LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + " to floor " + Integer.toString(destFloor));
		SchedulerDispatchMessage dispatchMsg = SchedulerDispatchMessage.newBuilder()
				.setDestFloor(destFloor)
				.setElevatorID(elevatorID)
				.setRequestID(requestID)
				.build();

		sendMessage(dispatchMsg, port, address);
	}
	
	/**
	 * Send a UDP message indicating elevator has arrived at a floor
	 * 
	 * @param ElevatorArrivedMessage message to be updated
	 */
	private void sendElevatorArrivedMessage(ElevatorArrivedMessage message, int requestID, int port, InetAddress address) throws IOException {
 		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder()
				.setElevatorID(message.getElevatorID())
				.setFloor(message.getFloor())
				.setRequestID(requestID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, port, address);
	}
	
	protected void sendStopDoorFaultSimulateFaultMessage(int elevatorID, int port, InetAddress address) throws IOException {
		SimulateFaultMessage msg = SimulateFaultMessage.newBuilder()
				.setFault(FaultType.DOORFAULT)
				.setElevatorID(elevatorID)
				.setTimeout(0)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, port, address);
	}
	
	/**
	 * Send a message saying a that an elevator has been registered
	 * 
	 * @param elevatorID the id of the registered elevator
	 * @param port the port of the elevator
	 * @param address the InetAddress
	 * @throws IOException an exception with UDP
	 */
	private void sendElevatorRegisterMessage(int elevatorID, int port, InetAddress address) throws IOException {
		ElevatorRegisterMessage msg = ElevatorRegisterMessage.newBuilder()
				.setElevatorID(elevatorID)
				.build();

		sendMessage(msg, port, address);
	}

	/**
	 * Assign the best elevator for the a request
	 * @param req the request
	 * @return the best elevator
	 */
    public Elevator assignBestElevator(ElevatorRequest req) {
    	if(elevators.size() == 0) {
    		return null;
    	}
    	
    	Elevator selectedElevator = null;
    	if(elevators.size() == 1) {
    		synchronized(elevators.get(0)){
    			elevators.get(0).addDestination(req);
    		}
    		selectedElevator = elevators.get(0);
    	} else {
    		Elevator best = null;
    		Iterator<Elevator> iter = elevators.iterator();
    		while (iter.hasNext()) {
	    		Elevator elevator = iter.next();
	    		if(best != null) {
		    		synchronized(elevator){
		    			synchronized(best){
		    	    		if(compareElevator(elevator, best, req)) {
		    	    			best = elevator;
		    	    		}
		    			}
		    		}
	    		} else {
	    			best = elevator;
	    		}
    		}
    		synchronized(best){
    			best.addDestination(req);
    		}
    		selectedElevator = best;
    	}
    	return selectedElevator;
    }

	/**
	 * Compare the elevator requests and their score
	 * @param e1 the first elevator
	 * @param e2 the second elevator
	 * @param request the request
	 * @return whether elevator one preferred over two
	 */
	private Boolean compareElevator(Elevator e1, Elevator e2, ElevatorRequest request) {
    	int e1Score = evaluateDirectionalScore(e1, request) - e1.getNumDestinations() + e1.isRequestInQueue(request)*2 + getSchedulableScore(e1);
    	int e2Score = evaluateDirectionalScore(e2, request) - e2.getNumDestinations() + e2.isRequestInQueue(request)*2 + getSchedulableScore(e2);
    	if (e1Score > e2Score) {
        	return true;
    	}
    	return false;
    }

	/**
	 * Evaluate the request priority using the direction as a factor
	 * 
	 * @param e the elevator
	 * @param request the new request
	 * @return the score returned
	 */
	private int evaluateDirectionalScore(Elevator e, ElevatorRequest request) {
    	int score = e.getCurrentFloor() - request.getFloor();
    	Direction direction = e.getlDirection();
    	if (score == 0 && direction != Direction.STATIONARY) {
    		return -9999 + e.getElevatorID();
    	}
    	if (score < 0 && direction == Direction.UP) {
    		score *= -1;
    	} else if (score < 0 && direction == Direction.DOWN) {
    		score = (numFloors - Math.abs(score)) * -1;
    	} else if (score > 0 && direction == Direction.UP) {
    		score = (numFloors - Math.abs(score)) * -1;
    	} else if (direction == Direction.STATIONARY) {
    		return Math.abs(score);
    	}
    	return score;
    }
	
	private int getSchedulableScore(Elevator e) {
		if (!e.isSchedulable()) {
			return -99999999;
		}
		return 0;
	}

	/**
	 * Stop all of the threads
	 */
	public void stopScheduler() {
    	isRunning = false;
    	this.closePbSocket();
    	listenerThread.interrupt();
    	schedulerThread.interrupt();
    }
	
	protected void hardFaultElevator(Elevator e) {
		if(e.getState() != ElevatorState.TIMEOUT) {
			return;
		}
		
		LOGGER.error("Elevator " + e.getElevatorID() + ": has timed out, removing elevator");
		elevators.remove(e);
		if(elevators.size() >= 1) {
			LOGGER.info("Resceduling Elevator " + e.getElevatorID() + ": external button requests to other elevators");
			ArrayList<ElevatorRequest> pending = e.getAllExternalRequest();
			for (ElevatorRequest r : pending) {
				assignBestElevator(r);
			}
		}
		//verifyElevatorTopRequests();
	}
	
	//Buggy as it can send a message to elevator while its in the arriving state
	protected void verifyElevatorTopRequests() {
		for(Elevator elevator : elevators) {
			synchronized(elevator){
				if(elevator.peekTopRequest() != null) {
					if(elevator.peekTopRequest().getFloor() != elevator.getCurrentDestination()) {
						if(elevator.isTimeoutTimerOff()) {
							System.out.println("Elevator " + elevator.getElevatorID() + " started timeout timer");
							elevator.startTimeoutTimer();
						}
						elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
						try {
							sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), elevator.peekTopRequest().getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * The main method for running the threads
	 * @param args
	 */
	public static void main(String[] args) {
		Scheduler s = null;
		
		try {
			s = new Scheduler(6969, 10);
		} catch (SocketException e) {
			e.printStackTrace();
			LOGGER.error("Socket creation failed!");
		}

		s.startListenerThread();
		s.startSchedulingThread();
	}
}