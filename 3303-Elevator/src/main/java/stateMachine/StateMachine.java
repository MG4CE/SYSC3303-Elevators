package stateMachine;

import java.io.IOException;
import java.util.logging.Logger;

import protoBufHelpers.ProtoBufMessage;

/**
 * State machine controller, responsible for handling transitions between state based on input
 * and the invocation of exit and entry actions.
 */
public class StateMachine {
	private State currentState;
	private final Logger LOGGER = Logger.getLogger(StateMachine.class.getName());
	
	/**
	 * Constructor to create the state machine
	 * 
	 * @param initialState first state
	 */
	public StateMachine(State initialState){
		this.currentState = initialState;
		LOGGER.info("Initializing State Machine at State: " + currentState.getClass().getName());
		this.currentState.entryActions(); // invoke entry actions if any
	}
	
	/**
	 * Updates the state machine based on the response of the current state to the 
	 * received message
	 * 
	 * @param message message sent to the state machine for processing
	 * @throws IOException
	 */
	public synchronized void updateFSM(ProtoBufMessage message) throws IOException {
		State prevState = getCurrentState();
		currentState = currentState.nextState(message);
		if (currentState != prevState) { // State change occurred!
			LOGGER.info("State Machine Moving to " + this.currentState.getClass().getName());
			prevState.exitActions(); // invoke exit actions if any
			this.currentState.entryActions(); // invoke entry actions if any
		}
	}
	
	/**
	 * Get the current state
	 * 
	 * @return current state
	 */
	public State getCurrentState() {
		return this.currentState;
	}
}
