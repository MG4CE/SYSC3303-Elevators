package commands;

import java.util.Calendar;

public class MotorMessage extends Command {
	int height;
	
	public MotorMessage(Calendar time, int elevatorHeight) {
		super(Calendar.getInstance());
		this.height = elevatorHeight;
	}
	
	public int getHeight() {
		return this.height;
	}
}
