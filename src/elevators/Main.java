package elevators;

public class Main {
    public static void main(String[] args) {
    	Scheduler s = new Scheduler();
    	FloorSubsystem fs = new FloorSubsystem(s);
    	Elevator e = new Elevator(s);
    	
    	Thread t1 = new Thread(s);
    	Thread t2 = new Thread(fs);
    	Thread t3 = new Thread(e);
    	
    	t1.start();
    	t2.start();
    	t3.start();
    }
}
