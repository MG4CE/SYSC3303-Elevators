package floorsubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;

import com.google.protobuf.InvalidProtocolBufferException;

import communication.ProtoBufMessage;
import communication.UDPHelper;
import message.*;
import scheduler.SchedulerBean;
import scheduler.SchedulerTCPServer;

/**
 * Responsible for reading input file and generating commands that will be sent to the scheduler and the elevator.
 * The scheduler will receive internal button commands, and the
 */
public class FloorSubsystem extends UDPHelper implements Runnable{

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(FloorSubsystem.class);  
    private ArrayList<ElevatorRequestMessage> elevatorInteriorRequestList;
    private String commandFile;
    private List<Integer> repliedMessages; // request IDs for messages which have been replied to
    private int schedulerPort;
    private InetAddress schedulerAddress;
    private boolean inputFileParsed;
    private HashMap<Integer, Long> startTimes; // start times for each request id
    private int requestIDcount; // total number of requests per file for timing purposes
    private long programStart;
    private long programEnd;
    private SchedulerBean beanToSendTimingMessagesTo;
    private final int TIMESERVERADDRESS = 119;

    /**
     * Create new instance of Floor Subsystem
     *
     * @param schedulerPort : The port where the scheduler will be listening for requests
     * @param commandFile : input text file containing the list of commands
     */
    public FloorSubsystem(int schedulerPort, String commandFile, InetAddress schedulerAddress) throws SocketException {
        super();
        this.schedulerPort = schedulerPort;
        this.commandFile = commandFile;
        this.elevatorInteriorRequestList = new ArrayList<>();
        this.repliedMessages = new ArrayList<>();
        this.schedulerAddress = schedulerAddress;
        this.inputFileParsed = false;
        this.startTimes = new HashMap<Integer, Long>();
        this.requestIDcount = 0;
        this.programEnd = 0;
        this.programStart = 0;
    }

    /**
     * Reads the input file and for every line it does 2 things:
     * 1. Create an external button request timer event, that schedules a (external) request message to be sent later.
     * 2. Append an internal button request to an array list.
     *
     * NOTE: Internal button requests are saved for later use. (when elevator arrival messages are received)
     *
     * @param timer Timer Task events are scheduled with this timer instance
     */
    public void readCommandsFile(Timer timer){
        Scanner s = null;
        Direction direction = null;
        int interiorFloorButton = 0;
        int exteriorFloorButton = 0;
        String line = null;
        int elevatorId = 0;
        int timeout = 0;
        FaultType faultType = null;
        boolean isFaultMessage = false;

        Calendar programStartTime = Calendar.getInstance();
        Calendar firstLineFileTime = null;
        Calendar currentLineFileTime = null;
        Calendar currentLineAdjustedTime = Calendar.getInstance();

        LOGGER.info("Reading commands from " + commandFile);

        // Initiate scanner and read the file
        try {
            s = new Scanner(new File(commandFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.info(e.getMessage());
            System.exit(1);
        }

        /* Parse each line in the input file
         * Each line generates 1 interior and 1 exterior request message
         * Exterior requests are schedule to be sent depending on the timestamp difference relative to runtime
         * Interior requests are stored in a list to be sent when elevator arrival message are received
         */
        while (s.hasNext()){
            try {
                line = s.nextLine();
                String[] lineParts = line.split(" ");
                // check if line is a fault command
                if (lineParts[0].equals("SF") || lineParts[0].equals("HF")){
                    isFaultMessage = true;
                }
                this.requestIDcount += 1; // Increment ID for next event

                // Save the first line of the input files time
                if(firstLineFileTime == null) {
                    // Fault messages have time stamp at index 1, regular command have at index 0
                    if(isFaultMessage){
                        currentLineFileTime = utils.Utils.stringToCalendar(lineParts[1]);
                    } else {
                    currentLineFileTime = utils.Utils.stringToCalendar(lineParts[0]);
                    }
                    firstLineFileTime = currentLineFileTime;
                }

                // Soft fault command
                if (lineParts[0].equals("SF") || lineParts[0].equals("HF")){
                    if (lineParts[0].equals("SF")){ // Soft fault
                        faultType = FaultType.DOORFAULT;
                        timeout = Integer.parseInt(lineParts[3]); // timeout for soft fault
                    } else { // hard fault
                        faultType = FaultType.ELEVATOR_UNRESPONSIVE;
                    }
                    currentLineFileTime = utils.Utils.stringToCalendar(lineParts[1]);
                    elevatorId = Integer.parseInt(lineParts[2]);

                    // Create Fault Message - Internal Fault Message
                    SimulateFaultMessage faultMessage = createSimulateFaultMessage(faultType, elevatorId, timeout);

                    // Create Timer Event - for EXTERIOR elevator button request
                    TimerTask task = makeFaultRequestTimer(faultMessage);

                    // Schedule Timer Event task in the future
                    long offset = getCalDifferenceMillis(firstLineFileTime, currentLineFileTime);
                    currentLineAdjustedTime.setTimeInMillis(programStartTime.getTimeInMillis() + offset);
                    timer.schedule(task,currentLineAdjustedTime.getTime());

                } else { // Normal command

                    // Get calendar time object for current input file line
                    currentLineFileTime = utils.Utils.stringToCalendar(lineParts[0]);
                    exteriorFloorButton = Integer.parseInt(lineParts[1]); // External Floor the command was called from
                    direction = utils.Utils.stringToDirection(lineParts[2]);// Get Floor Direction Enum
                    interiorFloorButton = Integer.parseInt(lineParts[3]);// Internal Elevator destination floor button

                    // Create Request - EXTERIOR elevator request message
                    ElevatorRequestMessage exteriorRequestMessage = createExteriorElevatorRequestMessage(exteriorFloorButton,this.requestIDcount,direction);

                    // Create Timer Event - for EXTERIOR elevator button request
                    TimerTask task = makeMessageRequestTimer(exteriorRequestMessage);

                    // Schedule Timer Event task in the future
                    long offset = getCalDifferenceMillis(firstLineFileTime, currentLineFileTime);
                    currentLineAdjustedTime.setTimeInMillis(programStartTime.getTimeInMillis() + offset);
                    timer.schedule(task,currentLineAdjustedTime.getTime());

                    // Add Interior Button request to the list
                    this.elevatorInteriorRequestList.add(createInteriorElevatorRequestMessage(interiorFloorButton, this.requestIDcount));

                }

            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        // Close the scanner
        s.close();

        LOGGER.info("Command file read complete!");
    }

    /**
     * Return the immediate time in milliseconds
     * @return Long : time right now in Millis.
     */
    private long getTimeNow() {
    	return System.currentTimeMillis();
    }
    
    /**
     * Creates timer that sends a request in the future
     * The Code inside the run() method is what will execute when the timer executes
     * Also keeps track of a starting time of when the request was sent out
     * @param requestMessage Elevator request message to be sent
     * @return Timer Task event
     */
    public TimerTask makeMessageRequestTimer(ElevatorRequestMessage requestMessage) {    	
        return new TimerTask() {
            @Override
            public void run() {
                try {
                	// Save start time of when this request is "executed"
                	long timeNow = getTimeNow();
                	startTimes.put(requestMessage.getRequestID(), timeNow);
                	
                	// If program start time has not been initiated, this is the first command to be executed
                	if (programStart == 0) {
                		programStart = getTimeNow();
                	}
                    sendExteriorElevatorRequest(requestMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * Creates timer that sends a request in the future
     * The Code inside the run() method is what will execute when the timer executes
     * @param faultMessage Elevator fault  message to be sent
     * @return Timer Task event
     */
    public TimerTask makeFaultRequestTimer(SimulateFaultMessage faultMessage) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    sendElevatorFaultMessage(faultMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Sends an INTERIOR elevator request message to the scheduler subsystem
     * This simulates an INTERIOR  elevator button being pressed OF ANY FLOOR
     * @param requestMessage Elevator Request Message to be sent
     */
    private void sendInteriorElevatorRequest(ElevatorRequestMessage requestMessage) throws IOException {
        LOGGER.debug("[FloorSubsystem] Sending interior button request (ReqID: " + requestMessage.getRequestID() + ")");
        LOGGER.info("[Elevator " + requestMessage.getElevatorID() 
        			+ "] Floor " + requestMessage.getFloor() 
        			+ " button pressed inside Elevator " + requestMessage.getElevatorID() 
        			+ " [Request ID: " + requestMessage.getRequestID() + "]");
        sendMessage(requestMessage, this.schedulerPort, this.schedulerAddress);
    }
    /**
     * Sends an elevator request message to the scheduler subsystem
     * This simulates an exterior elevator button being pressed at any floor
     * @param requestMessage Elevator Request Message to be sent
     */
    private void sendExteriorElevatorRequest(ElevatorRequestMessage requestMessage) throws IOException {
        LOGGER.info("[Floor " + requestMessage.getFloor() + "] " + requestMessage.getDirection()
                +" button pressed [Request ID: " + requestMessage.getRequestID() + "]");
        sendMessage(requestMessage, this.schedulerPort, this.schedulerAddress);
    }

    private void sendElevatorFaultMessage(SimulateFaultMessage faultMessage) throws IOException {
        LOGGER.info("[FloorSubsystem] Sending elevator " + faultMessage.getFault() +
                " fault to => [Elevator ID: "+ faultMessage.getElevatorID() + "]");
        sendMessage(faultMessage, this.schedulerPort, this.schedulerAddress);
    }

    /**
     * EXTERIOR elevator request message  is made (protobuf builder)
     * @param floor Floor for the request message
     * @param requestId ID of the request
     * @return Exterior elevator request message
     */
    private ElevatorRequestMessage createExteriorElevatorRequestMessage(int floor, int requestId,Direction direction) {
        return ElevatorRequestMessage.newBuilder()
                .setFloor(floor)
                .setButton(Button.EXTERIOR)
                .setDirection(direction)
                .setTimeStamp("Monkey Moment")
                .setRequestID(requestId)
                .build();
    }

    /**
     * INTERIOR elevator request message is made (protobuf builder)
     * @param floor Floor for the request message
     * @param requestId ID of the request
     * @return Interior elevator request message
     */
    private ElevatorRequestMessage createInteriorElevatorRequestMessage(int floor, int requestId) {
        return ElevatorRequestMessage.newBuilder()
                .setFloor(floor)
                .setButton(Button.INTERIOR)
                .setTimeStamp("Monkey Moment")
                .setRequestID(requestId)
                .build();
    }

    /**
     * INTERIOR elevator request message is made (protobuf builder)
     * Takes in elevatorID as well
     * @param floor Floor for the request message
     * @param requestId ID of the request
     * @param elevatorId ID of the elevator
     * @return Interior elevator request message
     */
    private ElevatorRequestMessage createInteriorElevatorRequestMessage(int floor, int requestId, int elevatorId) {
        return ElevatorRequestMessage.newBuilder()
                .setFloor(floor)
                .setButton(Button.INTERIOR)
                .setElevatorID(elevatorId)
                .setTimeStamp("Monkey Moment")
                .setRequestID(requestId)
                .build();
    }

    /**
     * Creates a hard or soft fault message.
     * Soft faults will utilize the timeout field
     * hard faults set timeout to 0
     * @param faultType The type of fault
     * @param elevatorID elevator that will receive the fault
     * @param timeout timeout for soft faults, will be ignored if hard fault
     * @return
     */
    private SimulateFaultMessage createSimulateFaultMessage(FaultType faultType, int elevatorID, int timeout){
        if (faultType == FaultType.ELEVATOR_UNRESPONSIVE){ // hard faults; timeout
            return SimulateFaultMessage.newBuilder()
                    .setFault(faultType)
                    .setElevatorID(elevatorID)
                    .build();
        } // Soft fault ; YES timeout
        return SimulateFaultMessage.newBuilder()
                .setFault(faultType)
                .setElevatorID(elevatorID)
                .setTimeout(timeout)
                .build();
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
        this.inputFileParsed = true;
        while(true) {
        	
            // Receive UPD message
            DatagramPacket recvMessage = null;
            try {
                recvMessage = receiveMessage();
            } catch (IOException e1) {
                e1.printStackTrace();
                LOGGER.error("Failed to receive data!");
                System.exit(1);
            }
            // Create Protobuf message
            ProtoBufMessage msg = null;
            try {
                msg = new ProtoBufMessage(recvMessage);
            } catch (InvalidProtocolBufferException e2) {
                e2.printStackTrace();
                LOGGER.error("Failed to convert received to protobuf type!");
                System.exit(1);
            }
            /* Received Message Handling

                1. Elevator arrival messages
                -> trigger directional lamps OFF (for the elevator shaft no longer being used)
                -> set elevator id before sending interior button press
                
                	1.1 (if arrival message contains a request ID for which the interior button has not been sent)
                	-> Send interior button press.
                	1.2 (if arrival message has ID for which the interioir button was already sent; request is done)
                	-> save total time it took to service that request and send to scheduler
                	-> send total input file execution time when the lastrequest is serviced
                	
                2. Elevator departure message
                -> trigger directional lamps ON (for the elevator shaft being used)
             */
            if (msg.isElevatorArrivedMessage()) { // 1. Elevator arrival messages
                ElevatorArrivedMessage arrivedMessage = msg.toElevatorArrivedMessage();
                int reqID = arrivedMessage.getRequestID();
                
                
                LOGGER.info("[Floor " + arrivedMessage.getFloor() + "] Elevator "
                        + arrivedMessage.getElevatorID () +" has arrived at floor  " +
                        " [ReqID: " + reqID + "]");

                LOGGER.info("[Elevator Shaft "+ arrivedMessage.getElevatorID() + "] Lamps OFF");

                if(!repliedMessages.contains(reqID)) { // 1.1
                    for(ElevatorRequestMessage req:elevatorInteriorRequestList) {
                        if (req.getRequestID() == reqID) {
                            repliedMessages.add(reqID);
                            try {

                                // Add the elevator ID
                                ElevatorRequestMessage newInteriorRequest = createInteriorElevatorRequestMessage(
                                        req.getFloor(),
                                        req.getRequestID(),
                                        arrivedMessage.getElevatorID());

                                sendInteriorElevatorRequest(newInteriorRequest);
                                LOGGER.info("[Elevator Shaft "+ arrivedMessage.getElevatorID() + "] Lamps ON");
                            } catch (IOException e) {
                                e.printStackTrace();
                                LOGGER.error("Failed to send button request!");
                                System.exit(1);
                            }
                        }
                    }
                } else {  // 1.2
                	// Find the start time for the reqID of the arrival message 
                	long serviceTime = getTimeNow()- startTimes.get(reqID);
                	String newMessage = "REQ-ID: " + reqID + "- Service Time: " + serviceTime;
                	try {
						this.sendByteArray(newMessage.getBytes(), TIMESERVERADDRESS, InetAddress.getByName("localhost"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	System.out.println(newMessage);


                	// If this is the last request to be serviced, send the total program execution time
                	if (this.requestIDcount == reqID) {
                		// If program start time has not been initiated, this is the first command to be executed
                    	long totalTime = getTimeNow() - programStart;
                    	String totalTimeMsg = "All Requests Final Service Time: " + totalTime;
                    	System.out.println(totalTimeMsg);
                    	try {
    						this.sendByteArray(totalTimeMsg.getBytes(), TIMESERVERADDRESS, InetAddress.getByName("localhost"));
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                	}
                }
            } else if (msg.isElevatorDepartureMessage()) { // 2. Elevator departure messages
                ElevatorDepartureMessage departureMessage = msg.toElevatorDepartureMessage();
                LOGGER.debug("[FloorSubsystem] Elevator departure message arrived (elevator ID "+ departureMessage.getElevatorID() + ")" );

                if (departureMessage.getDirection() == Direction.UP){
                    LOGGER.debug("[Elevator Shaft "+ departureMessage.getElevatorID() + "] Up Lamp ON");
                } else {
                	LOGGER.debug("[Elevator Shaft "+ departureMessage.getElevatorID() + "] Down Lamp ON");
                }

            } else {
                LOGGER.warn("Received unknown command!");
            }

            if (elevatorInteriorRequestList.isEmpty()) {
                break;
            }
        }
        LOGGER.info("Floor Subsystem is complete!");
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        InetAddress schedulerAddress = InetAddress.getLocalHost();
        int schedulerPort = 6969;
        String commandFile = "documents/input/input.txt";
        Thread floorThread = new Thread (new FloorSubsystem(schedulerPort, commandFile, schedulerAddress));

        floorThread.start();
    }
    
    /**
     * Checks if input file is parsed
     * @return Boolean
     */
    public boolean isInputFileParsed() {
        return inputFileParsed;
    }
}