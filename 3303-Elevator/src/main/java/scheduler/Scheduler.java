package scheduler;

import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.SchedulerDispatchMessage;
import protoBufHelpers.UDPHelper;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Scheduler extends UDPHelper {
    private final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
    private ArrayList<ElevatorRequestMessage> requestQueue;

    public Scheduler(int listenPort) throws SocketException {
        super(listenPort);
        this.requestQueue = new ArrayList<>();
    }
    
    public void startListenerThread() {
    	
    }
    
    public void startSchedulingThread() {
    	
    }

    void sendSchedulerDispatchMessage(int destFloor, int elevatorID) throws IOException {
        LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + "to floor " +
                Integer.toString(destFloor));
        SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
                .setDestFloor(destFloor)
                .setElevatorID(elevatorID)
                // TODO: SET ELEVATOR ID
                //TODO ADD TIMESTAMP
                .build();
        sendMessage(msg, 0);
    }
    
	public static void main(String[] args) {
	    Logger LOGGER = Logger.getLogger("Scheduler Main");
		Scheduler s = null;
		
		try {
			s = new Scheduler(6969);
		} catch (SocketException e) {
			e.printStackTrace();
			LOGGER.severe("Socket creation failed!");
		}
		
		s.startListenerThread();
		s.startSchedulingThread();
	}
}
