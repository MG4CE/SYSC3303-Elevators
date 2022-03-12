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
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

public class Scheduler extends UDPHelper {
    private final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
    private int numFloors;
    private ArrayList<DatagramPacket> messageQueue;
    private ArrayList<Elevator> elevators;
    private int elevatorIDCounter;
    private Thread schedulerThread, listenerThread;
    private Boolean isRunning;
    private int floorSubsystemPort;
    private InetAddress floorSubsystemAddress;
    
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
    
	public void startListenerThread() {
		listenerThread = new Thread(new Runnable() {
			public void run() {
				while (isRunning) {
					DatagramPacket msg = null;
					try {
						msg = receiveMessage();
					} catch (IOException e1) {
						e1.printStackTrace();
						LOGGER.severe("Failed to receive data from socket, stopping!");
						isRunning = false;
						schedulerThread.interrupt();
					}
					if(!listenerThread.isInterrupted() && msg != null) {
						synchronized (messageQueue) {
							messageQueue.add(msg);
						}
					}

				}
			}
		});
	}

	public void startSchedulingThread() {
    	schedulerThread = new Thread(new Runnable() {
    		public void run() {
    			while(isRunning) {
	    			synchronized(messageQueue) {
	    				while(messageQueue.isEmpty())
	    				{
							try {
								wait();
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
							LOGGER.info(e.getMessage());
							continue;
						}
	    				
	    				if(msg.isElevatorRequestMessage()) {
	    					ElevatorRequestMessage request = msg.toElevatorRequestMessage();
	    					
	    					if (floorSubsystemPort == -1 || floorSubsystemAddress == null) {
	    						floorSubsystemPort = packet.getPort();
	    						floorSubsystemAddress = packet.getAddress();
	    					}
	    					
	    					if(request.getButton().equals(Button.INTERIOR)) {
	    						for(Elevator elevator:elevators) {
	    							if(request.getElevatorID() == elevator.getElevatorID()) {
	    								elevator.addDestination(new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection()));
										if(elevator.popTopRequest().getFloor() == request.getFloor()) {
											try {
												sendSchedulerDispatchMessage(request.getFloor(), request.getRequestID(), request.getDirection(), request.getElevatorID(), elevator.getAddress());
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
	    							}
	    						}
	    					} else {
								//Exterior button press means we need to add the floor to the queue
								for(Elevator elevator:elevators) {
									if(request.getElevatorID() == elevator.getElevatorID()) {
										elevator.addDestination(new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection()));
									}
								}
							}
	    				} else if(msg.isElevatorRegisterMessage()) {
							ElevatorRegisterMessage message = msg.toElevatorRegisterMessage();
	    					elevators.add(new Elevator(packet.getPort(), elevatorIDCounter, message.getFloor(), packet.getAddress()));
	    					try {
								sendElevatorRegisterMessage(elevatorIDCounter, packet.getPort(), packet.getAddress());
							} catch (IOException e) {
								LOGGER.severe("Failed to send repsonse to elevator register message: " + e.getMessage());
								stopScheduler();
							}
	    					elevatorIDCounter++;
	    				} else if(msg.isElevatorArrivedMessage()) {
							ElevatorArrivedMessage message = msg.toElevatorArrivedMessage();
							try {
								sendMessage(message, floorSubsystemPort, floorSubsystemAddress);
							} catch (IOException e) {
								LOGGER.severe("Failed to forward elevator arrived message: " + e.getMessage());
							}
							for(Elevator elevator:elevators) {
								if(message.getElevatorID() == elevator.getElevatorID()) {
									try {
										sendLampMessage(message.getFloor(), floorSubsystemPort, elevator.getlDirection(), message.getElevatorID(), floorSubsystemAddress);
									} catch (IOException e) {
										e.printStackTrace();
									}
									elevator.setState(ElevatorState.STOPPED);
									if (!elevator.getFloorDestinations().isEmpty()) {
										try {
											sendSchedulerDispatchMessage(elevator.popTopRequest().getFloor(), elevator.getPort(), elevator.getlDirection(), elevator.getElevatorID(), elevator.getAddress());
										} catch (IOException e) {
											e.printStackTrace();
										}
									} else {
										break; 
									}
								}
							}
						} else if(msg.isElevatorDepartureMessage()) {
							ElevatorDepartureMessage message = msg.toElevatorDepartureMessage();
							for(Elevator elevator:elevators) {
								if(message.getElevatorID() == elevator.getElevatorID()) {
									elevator.setState(ElevatorState.MOVING);
								}
							}
						} else if(msg.isFloorSensorMessage()) {
							FloorSensorMessage request = msg.toFloorSensorMessage();
							for(Elevator elevator:elevators) {
								if(request.getElevatorID() == elevator.getElevatorID()) {
									elevator.setCurrentFloor(request.getFloor());
								}
							}
						}
					}	
	    		}
    		}
    	});
	}

	//if we have an elevator departing then we need to set the lamp
	private void sendLampMessage(int floor, int port, Direction direction, int elevatorID, InetAddress address) throws IOException {
		LampMessage lampMsg = LampMessage.newBuilder()
				.setFloor(floor)
				.setElevatorID(elevatorID)
				.setDirection(direction)
				.build();

		sendMessage(lampMsg, port, address);
	}

	//when an elevator arrives send another dispatch message, and another message is in queue
	//If the button pressed has highest priority
	private void sendSchedulerDispatchMessage(int destFloor, int port, Direction direction, int elevatorID, InetAddress address) throws IOException {
		LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + "to floor " +
				Integer.toString(destFloor));
		SchedulerDispatchMessage dispatchMsg = SchedulerDispatchMessage.newBuilder()
				.setDestFloor(destFloor)
				.setElevatorID(elevatorID)
				.build();

		sendMessage(dispatchMsg, port, address);
	}
	
	private void sendElevatorRegisterMessage(int elevatorID, int port, InetAddress address) throws IOException {
		ElevatorRegisterMessage msg = ElevatorRegisterMessage.newBuilder()
				.setElevatorID(elevatorID)
				.build();

		sendMessage(msg, port, address);
	}
	
    public void assignBestElevator(ElevatorRequest req) {
    	if(elevators.size() == 1) {
    		elevators.get(0).addDestination(req);
    	} else {
    		Elevator best = null;
    		Iterator<Elevator> iter = elevators.iterator();
    		while (iter.hasNext()) {
	    		Elevator elevator = iter.next();
	    		if (best == null || compareElevator(elevator, best, req)) {
	    			best = elevator;
	    		}
    		}
    	}
    }
    
    //TODO: Incorporate the direction of the request as a factor in score
    private Boolean compareElevator(Elevator e1, Elevator e2, ElevatorRequest request) {
    	int e1Score = evaluateDirectionalScore(e1, request) - e1.getNumDestinations();
    	int e2Score = evaluateDirectionalScore(e2, request) - e2.getNumDestinations();
    	if (e1Score > e2Score) {
        	return true;
    	}
    	return false;
    }
    
    //TODO: Incorporate the direction of the request as a factor in score
    private int evaluateDirectionalScore(Elevator e, ElevatorRequest request) {
    	int score = e.getCurrentFloor() - request.getFloor();
    	Direction direction = e.getlDirection();
    	if (score < 0 && direction == Direction.UP) {
    		score *= -1;
    	} else if (score < 0 && direction == Direction.DOWN) {
    		score = (numFloors - Math.abs(score)) * -1;
    	} else if (score > 0 && direction == Direction.UP) {
    		score = (numFloors - Math.abs(score)) * -1;
    	}
    	return score;
    }
    
    public void stopScheduler() {
    	isRunning = false;
    	this.closePbSocket();
    	listenerThread.interrupt();
    	schedulerThread.interrupt();
    }

	public static void main(String[] args) {
	    Logger LOGGER = Logger.getLogger("Scheduler Main");
		Scheduler s = null;
		
		try {
			s = new Scheduler(6969, 10);
		} catch (SocketException e) {
			e.printStackTrace();
			LOGGER.severe("Socket creation failed!");
		}
		
		s.startListenerThread();
		s.startSchedulingThread();
	}
}