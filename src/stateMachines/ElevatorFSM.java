package stateMachines;

import commands.Command;
import commands.ElevatorRequestCommand;
import commands.ElevatorSensorMessage;
import elevators.Direction;
import elevators.Elevator;

public class ElevatorFSM {

	/*
	 * Interface that defines the next state method for each state
	 */
	protected interface stateEvent {
		State nextState(Command cmd, Elevator elevator);
	}
	
	/*
	 * List of States, as well as events to switch state
	 */
	public enum State implements stateEvent {
		INIT {
			@Override public State nextState(Command cmd, Elevator elevator){
				return IDLE;
			}
		},
		IDLE {
			@Override public State nextState(Command cmd, Elevator elevator) {
				if (cmd instanceof ElevatorRequestCommand) {
					ElevatorRequestCommand c = (ElevatorRequestCommand)cmd;
					if (c.getFloor() == elevator.getCurrentFloor()) {
						return BOARDING;
					} else {
						elevator.setDestinationFloor(c.getFloor());
						if(elevator.getCurrentFloor() > c.getFloor()) {
							elevator.setDirection(Direction.DOWN);
						}else if (elevator.getCurrentFloor() < c.getFloor()){
							elevator.setDirection(Direction.UP);
						}else {
							elevator.setDirection(Direction.IDLE);
						}
						return MOVING;
					}
				}
				return IDLE; // other command
			}
		},
		BOARDING {
			@Override public State nextState(Command cmd, Elevator elevator){
				if (cmd instanceof ElevatorRequestCommand) {
					return IDLE;
				
				}
				
				return INIT;
			}
		},
		/*
		 * State for when elevator is in motion, moving towards target
		 * @NextState->Moving   : Not close to destination floor
		 * @NextState->Moving   : Higher Priority floor request made
		 * 			           	     ->ACTION: Update DestinationFloor
		 * @NextState->Arriving : 1 Floor away from target
		 */
		MOVING {
			@Override public State nextState(Command cmd, Elevator elevator){
				int currFloor = elevator.getCurrentFloor();
				int destFloor = elevator.getDestinationFloor();
				Direction dir = elevator.getDirection();
				
				// If a higher priority message comes in
				if (cmd instanceof ElevatorSensorMessage) {
					ElevatorSensorMessage c = (ElevatorSensorMessage)cmd;
					// check if destination is within one floor of current floor
					if (dir == Direction.UP && currFloor == destFloor - 1) {
						return ARRIVING;
					} else if (dir == Direction.DOWN && currFloor == destFloor + 1) {
						return ARRIVING;
					}	
				}
				// If a higher priority Command comes in
				if (cmd instanceof ElevatorRequestCommand) {
					ElevatorRequestCommand c = (ElevatorRequestCommand)cmd;
					elevator.setDestinationFloor(c.getFloor());
					return MOVING;
				}
				return MOVING;
			}
		},
		/*
		 * State when elevator is approaching destination floor
		 * @NextState->Arriving : Default state change
		 * @NextState->BOARDING : Arrived at destination floor 
		 */
		ARRIVING {
			@Override public State nextState(Command cmd, Elevator elevator){
				if (cmd instanceof ElevatorSensorMessage) {
					if (elevator.getDestinationFloor() == elevator.getCurrentFloor()){
						return BOARDING;
					}
				}
				return ARRIVING;
			}
		}
	}
	
	State currentState;
	
	public ElevatorFSM(){
		currentState = State.IDLE;
	}
	
	public State nextState(Command cmd, Elevator elevator) {
		this.currentState = currentState.nextState(cmd, elevator);
		return this.currentState;
	}

	public State getState() {
		return this.currentState;
	}
	
	
}
