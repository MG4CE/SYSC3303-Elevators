package elevators;

public class Main {
    public static void main(String[] args) throws InterruptedException {
    	Scheduler s = new Scheduler();
    	FloorSubsystem fs = new FloorSubsystem(s);
    	Elevator e = new Elevator(s);
    	
    	Thread theShedulatorThread = new Thread(s);
    	Thread theFloorSubsystemThread = new Thread(fs);
    	Thread theElevatorThread = new Thread(e);
    	
    	
    	theFloorSubsystemThread.start();
    	theFloorSubsystemThread.join();
    	//Schedular will only run once floorSubsystem has finished
    	//This is intentially designed to address a finish condition and to prevent a race condition in Schedular
    	theShedulatorThread.join();
    	theShedulatorThread.start();
    	theElevatorThread.start();
    }
}
