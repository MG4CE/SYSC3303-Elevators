package elevators;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import elevatorCommands.Direction;

public class Motor extends TimerTask {
	private final Logger LOGGER = Logger.getLogger(Motor.class.getName());
	final int TIME_PER_FLOOR = 3; // seconds
	final int TIME_PER_FLOOR_ARRIVING = 5;
	motorState currentState;
	Elevator elevator;
	Timer timer;
	Thread motorThread;

	private enum motorState{
		RUNNING,
		IDLE,
	}

	Motor(Elevator elevator) {
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
		LOGGER.info("Starting motor at full speed");
		Timer t = new Timer();
		this.currentState = motorState.RUNNING;
		timer = new Timer();
		timer.schedule(this, TIME_PER_FLOOR*1000, TIME_PER_FLOOR*1000);
	}


	void stopMotor() {
		LOGGER.info("Stopping motor");
		this.timer.cancel();
	}
}
