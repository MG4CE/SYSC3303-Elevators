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

/**
 * The Scheduler class will be running one of the main threads in the system.
 * It will be responsible for facilitating the sending of messages to and from the elevator
 * and the floor subsystem
 */
public class Scheduler extends UDPHelper {
	//A logger for all the message logs
    private final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
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
						LOGGER.severe("Failed to receive data from socket, stopping!");
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
	 */
	public void startSchedulingThread() {
    	schedulerThread = new Thread(new Runnable() {
    		public void run() {
    			while(isRunning) {
	    			synchronized(messageQueue) {
	    				while(messageQueue.isEmpty())
	    				{
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
							LOGGER.info(e.getMessage());
							continue;
						}
	    				
	    				if(msg.isElevatorRequestMessage()) {
	    					ElevatorRequestMessage request = msg.toElevatorRequestMessage();
	    					
	    					if (floorSubsystemPort == -1 || floorSubsystemAddress == null) {
	    						floorSubsystemPort = packet.getPort();
	    						floorSubsystemAddress = packet.getAddress();
	    					}
	    					
	    					if(request.getButton().equals(Button.EXTERIOR)) {
	    						ElevatorRequest eReq = new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection());
	    						Elevator elevator = assignBestElevator(eReq);
								if(elevator.peekTopRequest().getFloor() == request.getFloor() && elevator.peekTopRequest().getFloor() != elevator.getCurrentDestination()) {
									try {
										sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), request.getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
									} catch (IOException e) {
										e.printStackTrace();
									}
									elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
								}
	    					} else {
								System.out.println("Internal button received " + request.getRequestID() + " floor " + request.getFloor() + " elevator " + request.getElevatorID());
								for(Elevator elevator : elevators) {
									if(request.getElevatorID() == elevator.getElevatorID()) {
										System.out.println("add internal button to elevator");
										elevator.addDestination(new ElevatorRequest(request.getFloor(), request.getRequestID(), request.getDirection()));
										if(elevator.peekTopRequest().getFloor() == request.getFloor()) {
											System.out.println("dispatch");
											try {
												sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), request.getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
											} catch (IOException e) {
												e.printStackTrace();
											}
											elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
										}
									}
								}
							}
	    				}
						else if(msg.isElevatorRegisterMessage()) {
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
							for(Elevator elevator : elevators) {
								if(message.getElevatorID() == elevator.getElevatorID()) {
									try {
										sendElevatorArrivedMessage(message, elevator.peekTopRequest().getRequestID(), floorSubsystemPort, floorSubsystemAddress);
									} catch (IOException e) {
										LOGGER.severe("Failed to forward elevator arrived message: " + e.getMessage());
									}
									elevator.setState(ElevatorState.STOPPED);
									elevator.popTopRequest();
									if (!elevator.getFloorDestinations().isEmpty()){
										try {
											sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), elevator.getlDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
										} catch (IOException e) {
											e.printStackTrace();
										}
										elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
									} else {
										break; 
									}
								}
							}
						} else if(msg.isElevatorDepartureMessage()) {
							ElevatorDepartureMessage message = msg.toElevatorDepartureMessage();
							try {
								sendMessage(message, floorSubsystemPort, floorSubsystemAddress);
							} catch (IOException e) {
								e.printStackTrace();
							}
							for(Elevator elevator : elevators) {
								if(message.getElevatorID() == elevator.getElevatorID()) {
									elevator.setState(ElevatorState.MOVING);
								}
							}
						} else if(msg.isFloorSensorMessage()) {
							FloorSensorMessage request = msg.toFloorSensorMessage();
							for(Elevator elevator : elevators) {
								if(request.getElevatorID() == elevator.getElevatorID()) {
									elevator.setCurrentFloor(request.getFloor());
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
	 * if we have an elevator departing then we need to set the lamp
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
	 * when an elevator arrives send another dispatch message, and another message is in queue
	 * 	If the button pressed has the highest priority send a dispatch
	 * @param destFloor the destination floor
	 * @param port the port to send to
	 * @param direction the direction of the elevator
	 * @param elevatorID the elevator to which to send the command
	 * @param address The InetAddress
	 * @throws IOException an exception with UDP
	 */
	private void sendSchedulerDispatchMessage(int destFloor, int port, Direction direction, int requestID, int elevatorID, InetAddress address) throws IOException {
		LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + " to floor " +
				Integer.toString(destFloor));
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
	
	/**
	 * Send a message saying a that an elevator has been registered
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
    	Elevator selectedElevator = null;
    	if(elevators.size() == 1) {
    		elevators.get(0).addDestination(req);
    		selectedElevator = elevators.get(0);
    	} else {
    		Elevator best = null;
    		Iterator<Elevator> iter = elevators.iterator();
    		while (iter.hasNext()) {
	    		Elevator elevator = iter.next();
	    		if (best == null || compareElevator(elevator, best, req)) {
	    			best = elevator;
	    		}
    		}
    		best.addDestination(req);
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
    	int e1Score = evaluateDirectionalScore(e1, request) - e1.getNumDestinations();
    	int e2Score = evaluateDirectionalScore(e2, request) - e2.getNumDestinations();
    	if (e1Score > e2Score) {
        	return true;
    	}
    	return false;
    }

	/**
	 * Evaluate the request priority using the direction as a factor
	 * @param e the elevator
	 * @param request the new request
	 * @return the score returned
	 */
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

	/**
	 * Stop all of the threads
	 */
	public void stopScheduler() {
    	isRunning = false;
    	this.closePbSocket();
    	listenerThread.interrupt();
    	schedulerThread.interrupt();
    }

	/**
	 * The main method for running the threads
	 * @param args
	 */
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