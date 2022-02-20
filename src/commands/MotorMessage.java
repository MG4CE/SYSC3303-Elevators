package commands;

import java.util.Calendar;

/**
 * Message sent to the elevator by the motor to update location
 */
public class MotorMessage extends Command {
	
	int height;
	
	/**
	 * Constructor
	 * @param elevatorHeight
	 */
	public MotorMessage(int elevatorHeight) {
		super(Calendar.getInstance());
		this.height = elevatorHeight;
	}
	
	/**
	 * Get height from message
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
}
