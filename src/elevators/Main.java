package elevators;

public class Main {
    public static void main(String[] args) throws InterruptedException {
    	Scheduler s = new Scheduler();
    	FloorSubsystem fs = new FloorSubsystem(s, "data/input.txt");
    	Elevator e = new Elevator(s);
    	
    	Thread theShedulatorThread = new Thread(s);
    	Thread theFloorSubsystemThread = new Thread(fs);
    	Thread theElevatorThread = new Thread(e);
    	
    	theShedulatorThread.start();
    	theFloorSubsystemThread.start();
    	theElevatorThread.start();
    }
}
