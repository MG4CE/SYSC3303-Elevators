package stateMachine;

import java.io.IOException;

import protoBufHelpers.ProtoBufMessage;

public interface State {
	/**
	 * Action to be performed upon entry to a state
	 */
	abstract void entryActions();
	
	/**
	 * Action to be performed upon exit of a state
	 */
	abstract void exitActions();
	
	/**
	 * Decides state transitions of a state based on the a message
	 * 
	 * @param message input message
	 * @return next state
	 * @throws IOException
	 */
	abstract State nextState(ProtoBufMessage message) throws IOException;
}
