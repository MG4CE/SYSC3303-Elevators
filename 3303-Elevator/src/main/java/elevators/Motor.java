package elevators;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Motor {	
	protected final int TIME_PER_FLOOR = 3; // seconds
	protected final int TIME_PER_FLOOR_ARRIVING = 5;
	protected motorState currentState;
	protected Elevator elevator;
	protected Timer timer;
	protected Thread motorThread;
	protected TimerTask timerTask;

	private enum motorState{
		RUNNING,
		IDLE,
	}

	protected Motor(Elevator elevator) {
		this.elevator = elevator;
		this.currentState = motorState.IDLE;
		this.timer = null;
		this.timerTask = null;
	}

	void startMotor(){
		elevator.LOGGER.info("Starting motor at full speed");
		this.currentState = motorState.RUNNING;
		this.timer = new Timer();
		this.timer.schedule(makeTimerTask(), TIME_PER_FLOOR*1000, TIME_PER_FLOOR*1000);
	}
	
	void moveOneFloor() {
		this.timer = new Timer();
		this.timer.schedule(makeTimerTask(), TIME_PER_FLOOR*1000);
	}
	
	private TimerTask makeTimerTask() {
		return new TimerTask() {
        	@Override
        	public void run() {
        		try {
        			elevator.motorUpdate();
        		} catch (IOException e) {
        			elevator.LOGGER.severe("Motor failed to update floor status, exiting [" + e.getMessage() +"]");
        			System.exit(1);
        		}
        	}
        };
	}

	void stopMotor() {
		elevator.LOGGER.info("Stopping motor");
		this.currentState = motorState.IDLE;
		this.timer.cancel();
	}
}
