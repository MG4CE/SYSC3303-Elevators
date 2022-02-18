package elevators;

import components.VerticalLocation;

/**
 * For now the movement and stop of the motor is instant, but does simulate a constant
 * velocity movement between floors.
 */
public class Motor {

	public enum MotorState {
		CRUSING,
		STOPPED
	}
	
	private static final int VELOCITY_MPS = 4;
	private MotorState state;
	private VerticalLocation location;
	private Boolean messageReady;
	private Thread t;
	
	public Motor() {
		this.state = MotorState.STOPPED;
		this.location = new VerticalLocation();
		this.messageReady = false;
	}
		
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
	
	public Boolean isMessageReady() {
		return messageReady;
	}
	
	public VerticalLocation getLocation() {
		return location;
	}
	
	public void stopMotor() {
		this.state = MotorState.STOPPED;
	}
}
