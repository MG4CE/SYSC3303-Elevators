package stateMachine;

import java.io.IOException;

import pbHelpers.PbMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateMachine {
	private State currentState;
	private final Logger LOGGER = Logger.getLogger(StateMachine.class.getName());
	
	public StateMachine(State initialState){
		this.currentState = initialState;
		LOGGER.info("Initializing State Machine at State: " + currentState.getClass().getName());
		this.currentState.entryActions(); // invoke entry actions if anys
	}
	
	public void updateFSM(PbMessage message) throws IOException {
		State prevState = getCurrentState();
		currentState = currentState.nextState(message);
		if (currentState != prevState) { // State change occurred!
			prevState.exitActions(); // invoke exit actions if any
			this.currentState.entryActions(); // invoke entry actions if any
		}
		LOGGER.info("State Machine Moving to " + this.currentState.getClass().getName());
	}
	
	public State getCurrentState() {
		return this.currentState;
	}
}
