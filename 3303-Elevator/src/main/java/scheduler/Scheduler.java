package scheduler;

import elevatorCommands.*;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

public class Scheduler extends UDPHelper {
    private final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
    private int numFloors;
    private ArrayList<ProtoBufMessage> messageQueue;
    private ArrayList<Elevator> elevators;
    private int elevatorIDCounter;
    private Thread schedulerThread, listenerThread;
    
    
    public Scheduler(int listenPort, int numFloors) throws SocketException {
        super(listenPort);
        this.numFloors = numFloors;
        this.messageQueue = new ArrayList<>();
        this.elevators = new ArrayList<>();
        this.elevatorIDCounter = 1;
    }
    
    public void startListenerThread() {
    	
    	listenerThread =  new Thread(new Runnable() {
    		public void run() {
    		
    			while(true) {
 
    			try {
    				ProtoBufMessage recvMessage = new ProtoBufMessage(receiveMessage());
    				synchronized(recvMessage)
    				{
    					messageQueue.add(recvMessage);
    				}
    			} catch (IOException e1) {
    				e1.printStackTrace();
    				LOGGER.severe("Failed to receive data from socket!");
    				System.exit(1);
    			}
    			
    		  }
    		}
    	});
    }
    
	public void startSchedulingThread() {
    	schedulerThread = new Thread(new Runnable() {
    		public void run()
    		{
    			synchronized(messageQueue) {
    				while(messageQueue.isEmpty())
    				{
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
    				ProtoBufMessage msg = messageQueue.remove(0);
    				if(msg.isElevatorRequestMessage())
    				{
    					ElevatorRequestMessage request = msg.toElevatorRequestMessage();
    					if(request.getButton().equals(Button.INTERIOR))
    					{
    						for(Elevator elevator:elevators)
    						{
    							if(request.getElevatorID() == elevator.getElevatorID())
    							{
    								elevator.addDestination(new ElevatorRequest(request.getFloor(),request.getRequestID(), request.getDirection()));
    							}
    						}
    						//Insert into request array
    						//check if its the same as the top request floor, 
    					}
    					else
    					{}
    				}
    				else if(msg.isElevatorRegisterMessage())
    				{
    					
    				}
    				else if(msg.isElevatorArrivedMessage())
					{

					}
					else if(msg.isElevatorDepartureMessage())
					{

					}
					else if(msg.isFloorSensorMessage())
					{

					}
    				//check what type
    				//If not empty then get the mesage check priority and send if appropriate?

				}
    				
    		}
    	});

	}

	//if we have an elevator departing then we need to set the lamp
	private void sendLampMessage(int floor, int port, Direction direction, int elevatorID) throws IOException {
		LampMessage lampMsg = LampMessage.newBuilder()
				.setFloor(floor)
				//.setElevatorID(elevatorID)
				.setDirection(direction)
				.build();

		sendMessage(lampMsg,port);
	}

	//when an elevator arrives send another dispatch message, and another message is in queue
	//If the button pressed has highest priority
	private void sendDispatch()
	{}

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
    
    void sendSchedulerDispatchMessage(int destFloor, int elevatorID) throws IOException {
        LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + "to floor " +
                Integer.toString(destFloor));
        SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
                .setDestFloor(destFloor)
                .setElevatorID(elevatorID)
                //TODO: SET ELEVATOR ID
                //TODO ADD TIMESTAMP
                .build();
        sendMessage(msg, 0);
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