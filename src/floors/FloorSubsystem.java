package floors;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
	public void readCommandsFile(String cmdFile){
		//Initialize variables
		Scanner s = null;
		String time = null;
		Direction direction = null;
		int internalFloorButton = 0;
		int externalFloorButton = 0;
		String line = null;
		int requestID= 0;
		
		// Initiate scanner and read the file
		try {
			s = new Scanner(new File(cmdFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Read each line until empty
		while (s.hasNext()){
			try {
				
				// Parse first half of each command into a floor request, and the second half into a elevator request
			    // Ex: 00:01:00.1 1 Up 1
				// String time, int floor, String direction, int requestID)
				line = s.nextLine();
				String lineParts[] = line.split(" ");
			
				// Time command was recieved 
				time = lineParts[0]; 
				System.out.println(time);
				
				// Get Floor Direction Enum
				direction = Command.stringToDirection(lineParts[1]);
				
				// External Floor the command was called from
				externalFloorButton = Integer.parseInt(lineParts[2]);
				
				// Interna Elevator destination floor button 
				internalFloorButton = Integer.parseInt(lineParts[3]);
				
				// Make timer to send and make external floor request
				makeExternalButtonTimer(externalFloorButton,direction,requestID);
				
				// Make timer to make and send internal floor request
				
//				this.floorRequestList.add(new ExternalFloorBtnCommand(externalFloorButton, direction, requestID));
				this.elevatorRequestList.add(new InteriorElevatorBtnCommand(internalFloorButton, requestID));
				
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
	
	
	private long getCalendarDifference(Calendar startDate, Calendar endDate) {
	    long end = endDate.getTimeInMillis();
	    System.out.println("end: "+ end);
	    long start = startDate.getTimeInMillis();
	    System.out.println("start: "+ start);
	    return Math.abs(end - start);
	}
	
	/**
	 * Overriden run method
	 * Will extract one floor request and elevator request command from each line of input
	 * A timer will be created to send the floor requests at the correct time.
	 */
	@Override
	public void run(){
		readCommandsFile(this.commandFile);
		
		Timer timer  =  new Timer();
		
		System.out.println("Time1 : " + floorRequestList.get(0).getTimeString());
		System.out.println("Time2 : " + floorRequestList.get(1).getTimeString());
		
		System.out.println("Difference: " + getCalendarDifference(floorRequestList.get(0).getCalendarTime(), floorRequestList.get(1).getCalendarTime()));

	
		
		
		System.out.println("Floor subsystem terminated");
	}

}
