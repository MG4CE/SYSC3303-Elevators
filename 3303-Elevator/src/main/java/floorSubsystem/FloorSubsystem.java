package floorSubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.Button;
import elevatorCommands.Direction;
import elevatorCommands.ElevatorRequestMessage;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;

/**
 * This is a representation of a floor that an elevator will service
 * For this iteration, the subsystem will read an input file and create command objects from it
 * Then it will place those commands objects into the Scheduler
 */
public class FloorSubsystem extends UDPHelper implements Runnable{
	
    private final Logger LOGGER = Logger.getLogger(FloorSubsystem.class.getName());
    private ArrayList<ElevatorRequestMessage> elevatorRequestList;
    private String commandFile;
    private List<Integer> repliedMessages;
    private int schedulerPort;

    /**
     * Create new instance of Floor Subsystem
     *
     * @param scheduler
     * @param commandFile : input text file containing the list of commands
     */
    public FloorSubsystem(int schedulerPort, String commandFile) throws SocketException {
        super();
        this.schedulerPort = schedulerPort;
        this.commandFile = commandFile;
        this.elevatorRequestList = new ArrayList<>();
        this.repliedMessages = new ArrayList<>();
    }

    /**
     * Read input from a file and store each line as as command
     * Creates a Command object from each line and adds to command array list
     * @param path to file to read
     * @return ArrayList of commands
     */
    public void readCommandsFile(Timer timer){
        Scanner s = null;
        Direction direction = null;
        int internalFloorButton = 0;
        int externalFloorButton = 0;
        String line = null;
        int requestID = 0;

        Calendar programStartTime = Calendar.getInstance();
        Calendar firstLineFileTime = null;
        Calendar currentLineFileTime = null;
        Calendar currentLineAdjustedTime = Calendar.getInstance();
        
        LOGGER.info("Reading commands for " + commandFile);
        
        // Initiate scanner and read the file
        try {
            s = new Scanner(new File(commandFile));
        } catch (FileNotFoundException e) {
			e.printStackTrace();
           LOGGER.severe(e.getMessage());
           System.exit(1);
        }

        //Read each line until empty
        while (s.hasNext()){
            try {
                line = s.nextLine();
                String lineParts[] = line.split(" ");

                // Get calendar time object for current input file line
                currentLineFileTime = utils.Utils.stringToCalendar(lineParts[0]);

                // Save the first line of the input files time
                if(firstLineFileTime == null) {
                	//currentLineFileTime.add(Calendar.SECOND, 1);
                    firstLineFileTime = currentLineFileTime;
                }

                externalFloorButton = Integer.parseInt(lineParts[1]); // External Floor the command was called from
                direction = utils.Utils.stringToDirection(lineParts[2]);// Get Floor Direction Enum
                internalFloorButton = Integer.parseInt(lineParts[3]);// Internal Elevator destination floor button

                // Make timer event and schedule to send request in the future
                TimerTask task = makeExternalButtonTimer(externalFloorButton,direction,requestID);

                // Schedule the event at the adjusted time
                long offset = getCalDifferenceMillis(firstLineFileTime, currentLineFileTime);
                currentLineAdjustedTime.setTimeInMillis(programStartTime.getTimeInMillis() + offset);
                timer.schedule(task,currentLineAdjustedTime.getTime());
                
                this.elevatorRequestList.add(createElevatorRequestMessage(internalFloorButton, requestID));
                requestID += 1;
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        // Close the scanner
        s.close();
        
        LOGGER.info("Command file read complete!");
    }

    /**
     * Creates timer that sends a request in the future
     * @param request
     * @return timer to execute at the request time
     */
    public TimerTask makeExternalButtonTimer(int floor, Direction direction, int requestId) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    sendElevatorRequestMessage(floor, direction, requestId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * Creates and send button message
     *
     * @param floor
     * @throws IOException
     */
    private void sendElevatorRequestMessage(int floor, Direction direction, int requestId) throws IOException {
        LOGGER.info("External elevator button pressed on floor " + Integer.toString(floor) + " with direction " +
                direction.toString() + ", REQUEST_ID=" + Integer.toString(requestId));
        ElevatorRequestMessage msg = ElevatorRequestMessage.newBuilder()
                .setFloor(floor)
                .setButton(Button.EXTERIOR)
                .setDirection(direction)
                .setTimeStamp("Monkey Moment")
                .setRequestID(requestId)
                .build();
        sendMessage(msg, schedulerPort);
    }
    
    /**
     * Creates an interior button message
     *
     * @param floor
     * @throws IOException
     */
    private ElevatorRequestMessage createElevatorRequestMessage(int floor, int requestId) {
        ElevatorRequestMessage msg = ElevatorRequestMessage.newBuilder()
                .setFloor(floor)
                .setButton(Button.INTERIOR)
                .setTimeStamp("Monkey Moment")
                .setRequestID(requestId)
                .build();
        return msg;
    }

    /**
     * Calculates the difference between two calendar parameters in MiliSec
     * @param startDate first date to subtract from
     * @param endDate date being subtracted
     * @return the difference between the two dates in milliseconds
     */
    private long getCalDifferenceMillis(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return Math.abs(end - start);
    }

    /**
     * Will extract one floor request and elevator request command from each line of input
     * A timer will be created to send the floor requests at the correct time.
     */
    @Override
    public void run(){
        Timer timer = new Timer();
        readCommandsFile(timer);

        while(true) {
			DatagramPacket recvMessage = null;
			try {
				recvMessage = receiveMessage();
			} catch (IOException e1) {
				e1.printStackTrace();
				LOGGER.severe("Failed to receive data!");
				System.exit(1);
			}
			
			ProtoBufMessage msg = null;
			try {
				msg = new ProtoBufMessage(recvMessage);
			} catch (InvalidProtocolBufferException e2) {
				e2.printStackTrace();
				LOGGER.severe("Failed to convert received to protobuf type!");
				System.exit(1);
			} 
			
			if (msg.isElevatorArrivedMessage()) {
	            int reqID = msg.toElevatorArrivedMessage().getRequestID();
	            if(!repliedMessages.contains(reqID)) {
	                for(ElevatorRequestMessage req:elevatorRequestList) {
	                    if (req.getRequestID() == reqID) {
	                        repliedMessages.add(reqID);
	                        try {
								sendMessage(req, schedulerPort);
							} catch (IOException e) {
								e.printStackTrace();
								LOGGER.severe("Failed to send button request!");
								System.exit(1);
							}
	                    }
	                }
	            }
			} else {
				LOGGER.warning("Received unknown command!");
			}
			
			if (elevatorRequestList.isEmpty()) {
				break;
			}
        }
		LOGGER.info("Floor Subsytem is complete!");
    }
}