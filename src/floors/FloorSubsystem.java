package floors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import commands.Command;
import scheduler.Scheduler;
import commands.InteriorElevatorBtnCommand;
import elevators.Direction;
import commands.ExternalFloorBtnCommand;

/**
 * This is a representation of a floor that an elevator will service
 * For this iteration, the subsystem will read an input file and create command objects from it
 * Then it will place those commands objects into the Scheduler
 */
public class FloorSubsystem implements Runnable{

	//Instance Variables
	private Scheduler schedulator;
	private ArrayList<ExternalFloorBtnCommand> floorRequestList;
	private ArrayList<InteriorElevatorBtnCommand> elevatorRequestList;
	private String commandFile;
	
	/**
	 * Create new instance of Floor Subsystem
	 *
	 * @param scheduler
	 */
	public FloorSubsystem(Scheduler schedulator, String commandFile) {
		this.schedulator = schedulator;
		this.commandFile = commandFile;
		this.floorRequestList = new ArrayList<>();
		this.elevatorRequestList = new ArrayList<>();
	}

	/**
	 * Read input from a file and store each line as as command
	 * Creates a Command object from each line and adds to command array list
	 * @param path to file to read
	 * @return ArrayList of commands
	 */
	public void readCommandsFile(String cmdFile, Timer timer){
		//Initialize variables
		Scanner s = null;
		Direction direction = null;
		int internalFloorButton = 0;
		int externalFloorButton = 0;
		String line = null;
		int requestID = 0;
		
		String currentLineFileTimeStr = null;
		Command currentLineFileTimeCmd = null;
		
		Calendar programStartTime = Calendar.getInstance();
		System.out.println("Program start time: " + programStartTime.getTime());
		Calendar firstLineFileTime = null;
		Calendar currentLineFileTime = null;
		Calendar currentLineAdjustedTime = Calendar.getInstance();
		
		// Initiate scanner and read the file
		try {
			s = new Scanner(new File(cmdFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Read each line until empty
		while (s.hasNext()){
			try {
				line = s.nextLine();
				String lineParts[] = line.split(" ");
			
				// Get calendar time object for current input file line
				currentLineFileTime = new Command(lineParts[0]).getCalendarTime();
				System.out.println("Current Line File time : "+ currentLineFileTime.getTime());
				
				// Save the first line of the input files time 
				if(firstLineFileTime == null) {
					firstLineFileTime = currentLineFileTime;
					System.out.println("First Line File time :"+ firstLineFileTime.getTime());
				}
				
				externalFloorButton = Integer.parseInt(lineParts[1]); // External Floor the command was called from
				direction = Command.stringToDirection(lineParts[2]);// Get Floor Direction Enum
				internalFloorButton = Integer.parseInt(lineParts[3]);// Internal Elevator destination floor button 
				
				// Make timer event and schedule to send request in the future
				TimerTask task = makeExternalButtonTimer(externalFloorButton,direction,requestID);
				
				// Schedule the event at the adjusted time 
				long offset = getCalDifferenceMillis(firstLineFileTime, currentLineFileTime);
				currentLineAdjustedTime.setTimeInMillis(programStartTime.getTimeInMillis() + offset);
				
				
				System.out.println("Timer created at time: " + currentLineAdjustedTime.getTime());
				timer.schedule(task,currentLineAdjustedTime.getTime());
				
				//this.floorRequestList.add(new ExternalFloorBtnCommand(externalFloorButton, direction, requestID));
				//this.elevatorRequestList.add(new InteriorElevatorBtnCommand(internalFloorButton, requestID));
				
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		// Close the scanner
		s.close();
	}
	
	/**
	 * 
	 * @param request
	 * @return timer to execute at the request time
	 */
	public TimerTask makeExternalButtonTimer(int externalFloorButton, Direction direction, int requestID) {
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ExternalFloorBtnCommand externalRequest = new ExternalFloorBtnCommand(externalFloorButton,direction,requestID); 
				System.out.println("New Request created: " + externalRequest.getTimeStamp());
			}
		};
		
		return task;
	}
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private long getCalDifferenceMillis(Calendar startDate, Calendar endDate) {
	    long end = endDate.getTimeInMillis();
	    System.out.println("end:   "+ end);
	    long start = startDate.getTimeInMillis();
	    System.out.println("start: "+ start);
	    return Math.abs(end - start);
	}
	
	/**
	 * Will extract one floor request and elevator request command from each line of input
	 * A timer will be created to send the floor requests at the correct time.
	 */
	@Override
	public void run(){
		Timer timer = new Timer();
		
		readCommandsFile(this.commandFile, timer);
		
//		System.out.println("Difference: " + getCalendarDifference(floorRequestList.get(0).getCalendarTime(), floorRequestList.get(1).getCalendarTime()));
		
		System.out.println("Floor subsystem terminated");
	}

}
