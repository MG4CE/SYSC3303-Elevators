package stateMachine;

import java.io.IOException;

import pbHelpers.PbMessage;

class StateMachine {
	private State currentState;
	
	StateMachine(State initialState){
		this.currentState = initialState;
		this.currentState.entryActions(); // invoke entry actions if anys
	}
	
	void fireFSM(PbMessage message) throws IOException {
		State prevState = getCurrentState();
		currentState = currentState.nextState(message);
		if (currentState != prevState) { // State change occurred!
			prevState.exitActions(); // invoke exit actions if any
			this.currentState.entryActions(); // invoke entry actions if any
		}
	}
	
	State getCurrentState() {
		return this.currentState;
	}
}
