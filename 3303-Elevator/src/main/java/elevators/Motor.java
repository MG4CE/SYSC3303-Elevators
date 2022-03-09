package elevators;

import java.io.IOException;

import elevatorCommands.Direction;

public class Motor {
	final int FULL_SPEED_TIME_PER_FLOOR = 5; // seconds
	final int ARRIVING_TIME_PER_FLOOR = (int)(FULL_SPEED_TIME_PER_FLOOR * 1.5); // slightly slower

	motorState currentState;
	Elevator elevator;
	Thread motorThread;

	private enum motorState{
		FULL_SPEED,
		ARRIVING_SPEED,
		IDLE,
	}

	Motor(Elevator elevator){
		this.elevator = elevator;
		this.currentState = motorState.IDLE;

	}
	
	private final Runnable motorRunThread = () -> {
		while(this.currentState != motorState.IDLE) {
			try {
				wait(FULL_SPEED_TIME_PER_FLOOR * 1000);
			} catch (InterruptedException e) { // floor passed
				try {
					this.elevator.motorUpdate(); // tell elevator when floor hit!
				} catch (IOException ex) {
					System.out.println("failed to update elevator floor");
					ex.printStackTrace();
				}
			}
		}
	};
	
	void startMotor(Direction direction) throws IOException {
		if(this.currentState != motorState.IDLE) {
			throw new IOException("Elevator already running!\n");
		}
		this.currentState = motorState.FULL_SPEED;
		this.motorThread = new Thread(motorRunThread);
		this.motorThread.start();
	}
	
	void stopMotor() {
		this.currentState = motorState.IDLE;
		this.motorThread.notify(); // tell to stop waiting
	}
}
