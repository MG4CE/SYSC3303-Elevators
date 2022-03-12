package floorSubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.*;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;

/**
 * Responsible for reading input file and generating commands that will be sent to the scheduler and the elevator.
 * The scheduler will receive internal button commands, and the
 */
public class FloorSubsystem extends UDPHelper implements Runnable{

    private final Logger LOGGER = Logger.getLogger(FloorSubsystem.class.getName());
    private ArrayList<ElevatorRequestMessage> elevatorInteriorRequestList;
    private String commandFile;
    private List<Integer> repliedMessages; // request IDs for messages which have been replied to
    private int schedulerPort;
    private InetAddress schedulerAddress;

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
        System.out.println("opening file");

        Scanner s = null;
        Direction direction = null;
        int interiorFloorButton = 0;
        int exteriorFloorButton = 0;
        String line = null;
        int requestId = 0;

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

        /* Parse each line in the input file
         * Each line generates 1 interior and 1 exterior request message
         * Exterior requests are schedule to be sent depending on the timestamp difference relative to runtime
         * Interior requests are stored in a list to be sent when elevator arrival message are received
         */
        while (s.hasNext()){
            try {
                line = s.nextLine();
                String[] lineParts = line.split(" ");

                // Get calendar time object for current input file line
                currentLineFileTime = utils.Utils.stringToCalendar(lineParts[0]);

                // Save the first line of the input files time
                if(firstLineFileTime == null) {
                    //currentLineFileTime.add(Calendar.SECOND, 1);
                    firstLineFileTime = currentLineFileTime;
                }

                exteriorFloorButton = Integer.parseInt(lineParts[1]); // External Floor the command was called from
                direction = utils.Utils.stringToDirection(lineParts[2]);// Get Floor Direction Enum
                interiorFloorButton = Integer.parseInt(lineParts[3]);// Internal Elevator destination floor button

                // Create Request - EXTERIOR elevator request message
                ElevatorRequestMessage exteriorRequestMessage = createExteriorElevatorRequestMessage(exteriorFloorButton,requestId,direction);

                // Create Timer Event - for EXTERIOR elevator button request
                TimerTask task = makeMessageRequestTimer(exteriorRequestMessage);

                // Schedule Timer Event task in the future
                long offset = getCalDifferenceMillis(firstLineFileTime, currentLineFileTime);
                currentLineAdjustedTime.setTimeInMillis(programStartTime.getTimeInMillis() + offset);
                timer.schedule(task,currentLineAdjustedTime.getTime());

                // Add Interior Button request to the list
                this.elevatorInteriorRequestList.add(createInteriorElevatorRequestMessage(interiorFloorButton, requestId));
                requestId += 1; // Increment ID for next event
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
     * The Code inside the run() method is what will execute when the timer executes
     * @param requestMessage Elevator request message to be sent
     * @return Timer Task event
     */
    public TimerTask makeMessageRequestTimer(ElevatorRequestMessage requestMessage) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    sendExteriorElevatorRequest(requestMessage);
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
        LOGGER.info("[Floor] [Send] INTERIOR Button request - floor " + requestMessage.getFloor() +
                " REQUEST_ID=" + requestMessage.getRequestID() + "\n");
        sendMessage(requestMessage, this.schedulerPort, this.schedulerAddress);
    }

    /**
     * Sends an elevator request message to the scheduler subsystem
     * This simulates an exterior elevator button being pressed at any floor
     * @param requestMessage Elevator Request Message to be sent
     */
    private void sendExteriorElevatorRequest(ElevatorRequestMessage requestMessage) throws IOException {
        LOGGER.info("[Floor] [SEND] EXTERIOR elevator button pressed on floor " + requestMessage.getFloor() + " with direction " +
                requestMessage.getDirection() + ", REQUEST_ID=" + requestMessage.getRequestID() + "\n");

        sendMessage(requestMessage, this.schedulerPort, this.schedulerAddress);
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
                .setTimeStamp("Monkey Moment")
                .setRequestID(requestId)
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

        while(true) {
            // Receive UPD message
            DatagramPacket recvMessage = null;
            try {
                recvMessage = receiveMessage();
            } catch (IOException e1) {
                e1.printStackTrace();
                LOGGER.severe("Failed to receive data!");
                System.exit(1);
            }
            // Create Protobuf message
            ProtoBufMessage msg = null;
            try {
                msg = new ProtoBufMessage(recvMessage);
            } catch (InvalidProtocolBufferException e2) {
                e2.printStackTrace();
                LOGGER.severe("Failed to convert received to protobuf type!");
                System.exit(1);
            }

            /*
                Received Message Handling

                1. Elevator arrival messages
                -> trigger directional lamps OFF (for the elevator shaft no longer being used)
                -> set elevator id before sending interior button press
                -> Send interior button press.

                2. Elevator departure message
                -> trigger directional lamps ON (for the elevator shaft being used)
             */
            if (msg.isElevatorArrivedMessage()) { // 1. Elevator arrival messages
                ElevatorArrivedMessage arrivedMessage = msg.toElevatorArrivedMessage();
                int reqID = arrivedMessage.getRequestID();
                LOGGER.info("[FloorSubsystem] Received Elevator Arrived Message (ReqID: " + reqID + ")");

                LOGGER.info("[Elevator "+ arrivedMessage.getElevatorID() + "] Elevator Lamps OFF");

                if(!repliedMessages.contains(reqID)) { // 2. Elevator departure message
                    for(ElevatorRequestMessage req:elevatorInteriorRequestList) {
                        if (req.getRequestID() == reqID) {
                            repliedMessages.add(reqID);
                            try {
                                LOGGER.info("[FloorSubsystem] Sending interior button request (ReqID: " + reqID + ")");

                                // Add the elevator ID
                                ElevatorRequestMessage newInteriorRequest = createInteriorElevatorRequestMessage(
                                        req.getFloor(),
                                        req.getRequestID(),
                                        arrivedMessage.getElevatorID());

                                sendInteriorElevatorRequest(newInteriorRequest);
                            } catch (IOException e) {
                                e.printStackTrace();
                                LOGGER.severe("Failed to send button request!");
                                System.exit(1);
                            }
                        }
                    }
                }
            } else if (msg.isElevatorDepartureMessage()) { // 2. Elevator departure messages
                ElevatorDepartureMessage departureMessage = msg.toElevatorDepartureMessage();
                LOGGER.info("[FloorSubsystem] Elevator departure message arrived (elevator ID "+ departureMessage.getElevatorID() + ")" );

                if (departureMessage.getDirection() == Direction.UP){
                    LOGGER.info("[Elevator "+ departureMessage.getElevatorID() + "] Up Lamp ON");
                } else {
                    LOGGER.info("[Elevator "+ departureMessage.getElevatorID() + "] Down Lamp ON");
                }

            } else {
                LOGGER.warning("Received unknown command!");
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
        String commandFile = "src/main/java/input/input.txt";

        Thread floorThread = new Thread (new FloorSubsystem(schedulerPort, commandFile, schedulerAddress));

        floorThread.start();
    }
}