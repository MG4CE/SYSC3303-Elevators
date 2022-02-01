package elevators;

import java.util.Date;
import java.util.List;
import java.util.Queue;

public class Elevator implements Runnable {
	
	private int floor;
	private Date time; 
	private boolean available;
	private boolean finished;
	private final Queue<Message> theScheduler;
	
	
	public Elevator(Queue<Message> theScheduler) {
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
			Message message = theScheduler.poll();
			if(!isNull(message)) {
			goToFloorAndSetTime(message);
			}
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
