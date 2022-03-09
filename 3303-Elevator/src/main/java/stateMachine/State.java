package stateMachine;

import java.io.IOException;

import protoBufHelpers.ProtoBufMessage;

public interface State {
	abstract void entryActions();
	abstract void exitActions();
	abstract State nextState(ProtoBufMessage message) throws IOException;
}
