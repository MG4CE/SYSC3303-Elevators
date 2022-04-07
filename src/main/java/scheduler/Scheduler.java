package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


import org.apache.logging.log4j.LogManager;

import com.google.protobuf.InvalidProtocolBufferException;

import communication.ProtoBufMessage;
import communication.UDPHelper;

/**
 * The Scheduler class will be running one of the main threads in the system.
 * It will be responsible for facilitating the sending of messages to and from the elevator
 * and the floor subsystem
 */
public class Scheduler extends UDPHelper {
    protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Scheduler.class);  
    protected int numFloors;
    protected ArrayList<DatagramPacket> messageQueue;
    protected ArrayList<ElevatorControl> elevators;
    protected int elevatorIDCounter;
    protected Thread schedulerThread, listenerThread;
    protected Boolean isRunning;
    protected int floorSubsystemPort;
    protected InetAddress floorSubsystemAddress;
    protected SchedulerTCPServer backendForDash;
    
	/**
	 * The constructor for the Scheduler
	 * 
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
	    					MessageHandlers.handleElevatorRequestMessage(s, packet, msg);
	    				} else if(msg.isElevatorRegisterMessage()) {
	    					MessageHandlers.handleElevatorRegisterMessage(s, packet, msg);
	    				} else if(msg.isElevatorArrivedMessage()) {
	    					MessageHandlers.handleElevatorArrivedMessage(s, packet, msg);
						} else if(msg.isElevatorDepartureMessage()) {
							MessageHandlers.handleElevatorDepartureMessage(s, packet, msg);
						} else if(msg.isFloorSensorMessage()) {
							MessageHandlers.handleFloorSensorMessage(s, packet, msg);
						} else if(msg.isElevatorSimulateFaultMessage()) {
							MessageHandlers.handleElevatorSimulateFaultMessage(s, packet, msg);
						} else if(msg.isElevatorFaultMessage()) {
							MessageHandlers.handleElevatorFaultMessage(s, packet, msg);
						}
					}	
	    		}
    		}
    	});
		schedulerThread.start();
	}

	/**
	 * A method used in testing
	 * 
	 * @param e The elevator
	 */
	public void addToElevators(ElevatorControl e) {
		elevators.add(e);
	}

	/**
	 * Get if the Scheduler is running
	 * 
	 * @return
	 */
	public Boolean getIsRunning() {
		return isRunning;
	}
	
	/**
	 * Added a back-end TCP server to the scheduler
	 * 
	 * @param server SchedulerTCPServer
	 */
	void addServerToScheduler(SchedulerTCPServer server) {
		this.backendForDash = server;
	}
	
	/**
	 * Stop all of the threads
	 */
	public void stopScheduler() {
    	listenerThread.interrupt();
    	isRunning = false;
    	this.closePbSocket();
    	schedulerThread.interrupt();
    }
	
	/**
	 * Get all elevators registered with the scheduler
	 * 
	 * @return ArrayList<ElevatorControl>
	 */
	public ArrayList<ElevatorControl> getElevatorControl(){
		return this.elevators;
	}
	
	/**
	 * Stop all of the threads
	 */
	protected void stopSchedulerThreads() {
		isRunning = false;
		listenerThread.interrupt();
		schedulerThread.interrupt();
	}

	/**
	 * The main method for running the threads
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scheduler s = null;
		SchedulerTCPServer ss = null;
		TimeMessageServer ts = null;
		Thread schedServer= null;
		Thread timeServer = null;
		
		
		try {
			s = new Scheduler(6969, 22);
			ss = new SchedulerTCPServer(s);
			ts = new TimeMessageServer(ss);
			schedServer = new Thread(ss, "Server");
			timeServer = new Thread(ts, "TimeServer");
		} catch (SocketException e ) {
			e.printStackTrace();
			LOGGER.error("Socket creation failed!");
		} catch (IOException e) {
			LOGGER.error("Scheduler failed");
			
		}
		
		s.startListenerThread();
		s.startSchedulingThread();
		schedServer.start();
		timeServer.start();
	}
	
	
}