package statemachine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import communication.ProtoBufMessage;

class StateMachineTest {
	StateMachine sm;
	boolean hasEntered, hasLeft;
	State testState1, testState2;
	
	@BeforeEach
	void init() {
		hasEntered = false;
		hasLeft = false;
		
		testState1 = new State() {

			@Override
			public void entryActions() {
				System.out.println("entering test state1");
				hasEntered = true;
			}

			@Override
			public void exitActions() {
				hasLeft = true;
				System.out.println("leaving test state1");
			}

			@Override
			public State nextState(ProtoBufMessage message) throws IOException {
				return testState2;
			}
			
		};
		
		testState2 = new State() {

			@Override
			public void entryActions() {
				System.out.println("entering test state2");
				hasEntered = true;
			}

			@Override
			public void exitActions() {
				hasLeft = true;
				System.out.println("leaving test state2");
			}

			@Override
			public State nextState(ProtoBufMessage message) throws IOException {
				return null;
			}
			
		};
		
		sm = new StateMachine(testState1);
	}
	
	@Test
	void testFSMInit() {
		assertEquals(sm.getCurrentState(), testState1);
	}
	
	@Test
	void testFSMEnterAction() {
		assertTrue(hasEntered);
	}
	
	@Test
	void testFSMLeaveAction() throws IOException {
		sm.updateFSM(null);
		assertTrue(hasLeft);
	}
	
	@Test
	void testFSMGetState() throws IOException {
		assertEquals(sm.getCurrentState(), testState1);
		sm.updateFSM(null);
		assertEquals(sm.getCurrentState(), testState2);
	}

}
