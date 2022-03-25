package elevators;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Elevator motor, which handles the movement between floors and the timing of the movement
 */
public class Motor {	
	protected final int TIME_PER_FLOOR = 3; //seconds
	protected final int TIME_PER_FLOOR_ARRIVING = 5; //TODO: needs to be used for in arriving state
	protected motorState currentState;
	protected Elevator elevator;
	protected Timer timer;
	protected Thread motorThread;
	protected TimerTask timerTask;

	/**
	 * Motor state
	 */
	public enum motorState{
		RUNNING,
		IDLE,
	}

	/**
	 * Constructor 
	 * 
	 * @param elevator instance of elevator for communication
	 */
	protected Motor(Elevator elevator) {
		this.elevator = elevator;
		this.currentState = motorState.IDLE;
		this.timer = null;
		this.timerTask = null;
	}

	/**
	 * Start the motor be creating a recurring timer task that triggers every time we pass a floor
	 */
	protected void startMotor(){
		elevator.LOGGER.info("Starting motor at full speed");
		this.currentState = motorState.RUNNING;
		this.timer = new Timer();
		this.timer.schedule(makeTimerTask(), TIME_PER_FLOOR*1000, TIME_PER_FLOOR*1000);
	}
	
	/**
	 * Move one floor by triggering the timer task only once
	 */
	protected void moveOneFloor() {
		this.timer = new Timer();
		this.timer.schedule(makeTimerTask(), TIME_PER_FLOOR*1000);
	}
	
	/**
	 * Time task that updates the elevator current floor of the elevator
	 */
	private TimerTask makeTimerTask() {
		return new TimerTask() {
        	@Override
        	public void run() {
        		try {
        			elevator.motorUpdate();
        		} catch (IOException e) {
        			elevator.LOGGER.info("Motor failed to update floor status, exiting [" + e.getMessage() +"]");
        			System.exit(1);
        		}
        	}
        };
	}

	/**
	 * Stop the motor by disabling the timer
	 */
	protected void stopMotor() {
		elevator.LOGGER.info("Stopping motor");
		this.currentState = motorState.IDLE;
		this.timer.cancel();
	}
}
