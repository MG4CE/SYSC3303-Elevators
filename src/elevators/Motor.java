package elevators;

import components.VerticalLocation;

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
	
	private static final int VELOCITY_MPS = 4;
	private MotorState state;
	private VerticalLocation location;
	private Boolean messageReady;
	private Thread t;
	
	/**
	 * Constructor
	 */
	public Motor() {
		this.state = MotorState.STOPPED;
		this.location = new VerticalLocation();
		this.messageReady = false;
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
					try {
						Thread.sleep(VELOCITY_MPS*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized(location) {
						while(messageReady) {
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							if(direction == Direction.UP) {
								location.increment(VELOCITY_MPS);
							} else {
								location.increment(VELOCITY_MPS * -1);
							}
						}
						messageReady = true;
						notifyAll();
					}
				}
		    }
		});  
		t.start();
	}
	
	/**
	 * Returns a boolean signal indicating if a location update is ready
	 * @return Boolean
	 */
	public Boolean isMessageReady() {
		return messageReady;
	}
	
	/**
	 * Resets the location update signal
	 */
	public void resetMessageReady() {
		this.messageReady = false;
	}
	
	/**
	 * Get VerticalLocation (requires synchronization when accessed)
	 * @return VerticalLocation
	 */
	public VerticalLocation getLocation() {
		return location;
	}
	
	/**
	 * Stop the motor
	 */
	public void stopMotor() {
		this.state = MotorState.STOPPED;
	}
}
