package elevators;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class FloorSubsystem implements Runnable{

	private Scheduler schedulator;
	private ArrayList<Command> commandList;
	private String commandFile;
	private Boolean isFileOpen;

	/**
	 * Create new instance of Floor Subsystem
	 *
	 * @param scheduler
	 */
	public FloorSubsystem(Scheduler schedulator, String commandFile) {
		this.schedulator = schedulator;
		this.commandList = new ArrayList<Command>();
		this.commandFile = commandFile;
		this.isFileOpen = false;
	}

	/**
	 * Read input from a file and store each line as as command
	 * Creates a Command object from each line and adds to command array list
	 * @param path to file to read
	 * @return ArrayList of commands
	 */
	public ArrayList<Command> readCommandsFile(String cmdFile){
		Scanner s = null;
		ArrayList<Command> cmdList = new ArrayList<Command>();
		String time = null;
		String direction = null;
		int floor = 0;
		int selectedFloor = 0;
		String line = null;

		// Initiate scanner
		try {
			s = new Scanner(new File(cmdFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (s.hasNext()){
			try {
				line = s.nextLine();
				String lineParts[] = line.split(" ");
				
				time = lineParts[0];
				floor = Integer.parseInt(lineParts[1]); 
				direction = lineParts[2]; 
				selectedFloor = Integer.parseInt(lineParts[3]); 
				cmdList.add(new Command(time,floor,direction,selectedFloor));
			} catch (Exception e) {
				return null; // failed to parse! invalid File!
			}
		}
		s.close();
		return cmdList;
	}

	@Override
	public void run(){
		commandList = readCommandsFile(this.commandFile);
		Command terminateCommand = new Command("0:0:0.0", -1, "up", -1);
		commandList.add(terminateCommand);
		commandList.forEach((n) -> schedulator.addCommand(n));
		System.out.println("Floor subsystem terminated");
	}

}
