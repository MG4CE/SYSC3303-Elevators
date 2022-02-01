package elevators;

import java.util.Date;

public class Elevator implements Runnable {
	
	private int floor;
	private Date time; 
	private boolean available;
	private boolean finished;
	private final Scheduler theScheduler;
	
	
	public Elevator(Scheduler theScheduler) {
		this.theScheduler = theScheduler;
	}
	
	
	@Override
	public void run() {
		
		while(!finished) {
			runElevator();
		}
		
	}
	
	private void runElevator() {
		if(available) {
			goToFloorAndSetTime(theScheduler.getMessage());
		}
	}
	
	private void goToFloorAndSetTime(Message message) {
		setFloor(message.getFloor());
		setTime(message.getTime());
		//This is to simulate
		Thread.sleep(100);
		
	}
	
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}

	
	
	
	
	
}
