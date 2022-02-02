package elevators;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class FloorSubsystem implements Runnable{

	private Scheduler schedulator;
	private ArrayList<Command> commandList;

	/**
	 * Create new instance of Floor Subsystem
	 *
	 * @param scheduler
	 */
	public FloorSubsystem(Scheduler schedulator) {
		this.schedulator = schedulator;
		this.commandList = new ArrayList<Command>();
		commandList = readCommandsFile();
	}

	/**
	 * Read input from a file and store each line as as command
	 * Creates a Command object from each line and adds to command array list
	 * @return ArrayList of commands
	 */
	private ArrayList<Command> readCommandsFile(){
		Scanner s = null;
		ArrayList<Command> cmdList = new ArrayList<Command>();
		String time = null;
		String direction = null;
		int floor = 0;
		int selectedFloor = 0;
		String line = null;

		// Initiate scanner
		try {
			s = new Scanner(new File("src/elevators/input.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (s.hasNext()){
			
			line = s.nextLine();
			String lineParts[] = line.split(" ");
			
			time = lineParts[0];
			floor = Integer.parseInt(lineParts[1]); 
			direction = lineParts[2]; 
			selectedFloor = Integer.parseInt(lineParts[3]); 

			cmdList.add(new Command(time,floor,direction,selectedFloor));
		}
		s.close();
		return cmdList;
	}

	@Override
	public void run(){
		commandList.forEach((n) -> schedulator.addCommand(n));
	}

}
