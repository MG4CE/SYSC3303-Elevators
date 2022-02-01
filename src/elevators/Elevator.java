package elevators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

import static java.util.Objects.isNull;

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
			Command command = theScheduler.getCommand();
			if(!isNull(command)) {
			goToFloorAndSetTime(command);
			}
		}
	}
	
	private void goToFloorAndSetTime(Command message) {
		setFloor(message.getFloor());
		SimpleDateFormat formatter6=new SimpleDateFormat("hh:mm:ss.mmm"); 
		try {
			setTime(formatter6.parse(message.getTimestamp()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//This is to simulate
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
