package stateMachine;

import java.io.IOException;

import pbHelpers.PbMessage;

public interface State {
	abstract void entryActions();
	abstract void exitActions();
	abstract State nextState(PbMessage message) throws IOException;
}
