package elevators;

public class Main {
	/**
	 * Main method to be ran
	 * @param args
	 * @throws InterruptedException
	 */
    public static void main(String[] args) throws InterruptedException {
    	
    	
    	//Create objects
    	Scheduler s = new Scheduler();
    	FloorSubsystem fs = new FloorSubsystem(s, "data/input.txt");
    	Elevator e = new Elevator(s);
    	
    	//Set up threads
    	Thread theShedulatorThread = new Thread(s);
    	Thread theFloorSubsystemThread = new Thread(fs);
    	Thread theElevatorThread = new Thread(e);
    	
    	//Run threads
    	theShedulatorThread.start();
    	theFloorSubsystemThread.start();
    	theElevatorThread.start();
    }
}
