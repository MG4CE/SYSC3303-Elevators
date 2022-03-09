package stateMachine;

import java.io.IOException;

import pbHelpers.PbMessage;

public class StateMachine {
	private State currentState;
	
	public StateMachine(State initialState){
		this.currentState = initialState;
		this.currentState.entryActions(); // invoke entry actions if anys
	}
	
	public void fireFSM(PbMessage message) throws IOException {
		State prevState = getCurrentState();
		currentState = currentState.nextState(message);
		if (currentState != prevState) { // State change occurred!
			prevState.exitActions(); // invoke exit actions if any
			this.currentState.entryActions(); // invoke entry actions if any
		}
	}
	
	public State getCurrentState() {
		return this.currentState;
	}
}
