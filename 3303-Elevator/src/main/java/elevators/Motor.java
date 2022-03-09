package elevators;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Motor extends TimerTask {	
	protected final int TIME_PER_FLOOR = 3; // seconds
	protected final int TIME_PER_FLOOR_ARRIVING = 5;
	protected motorState currentState;
	protected Elevator elevator;
	protected Timer timer;
	protected Thread motorThread;

	private enum motorState{
		RUNNING,
		IDLE,
	}

	protected Motor(Elevator elevator) {
		this.elevator = elevator;
		this.currentState = motorState.IDLE;
	}

	@Override
	public void run() {
		try {
			elevator.motorUpdate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	void startMotor(){
		elevator.LOGGER.info("Starting motor at full speed");
		this.currentState = motorState.RUNNING;
		timer = new Timer();
		timer.schedule(this, TIME_PER_FLOOR*1000, TIME_PER_FLOOR*1000);
	}


	void stopMotor() {
		elevator.LOGGER.info("Stopping motor");
		this.currentState = motorState.IDLE;
		this.timer.cancel();
	}
}
