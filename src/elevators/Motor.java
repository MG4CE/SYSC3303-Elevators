package elevators;

import commands.MotorMessage;


/**
 * Motor controlling movement of the elevator
 * 
 * For now the movement and stop of the motor is instant, but does simulate a constant
 * velocity movement between floors.
 */
public class Motor {

	/**
	 * States of the motor
	 */
	public enum MotorState {
		CRUSING,
		STOPPED
	}
	
	private static final int VELOCITY_MPS = 1;
	private MotorState state;
	private Thread t;
	private int height = 0;
	private Elevator elevator;
	
	/**
	 * Constructor
	 */
	public Motor(Elevator elevator) {
		this.state = MotorState.STOPPED;
		this.elevator = elevator;
	}
	
	/**
	 * Given a direction, transition into the CRUSING state and move
	 * in the selected direction.
	 * @param direction
	 */
	public void move(Direction direction) {
		this.state = MotorState.CRUSING;
		t = new Thread(new Runnable() {
		    @Override
		    public void run() {
				while(state == MotorState.CRUSING) {
					/*try {
						Thread.sleep(VELOCITY_MPS*1000*ArrivalSensor.DISTANCE_BETWEEN_FLOORS_METERS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
					if (state == MotorState.STOPPED) {
						break;
					}
					if(direction == Direction.UP) {
						height += VELOCITY_MPS;
					} else {
						height -= VELOCITY_MPS;
					}
					elevator.elevatorPutCommand(new MotorMessage(height));
				}
		    }
		});  
		t.start();
	}

	/**
	 * Stop the motor
	 */
	public void stopMotor() {
		this.state = MotorState.STOPPED;
	}
}
