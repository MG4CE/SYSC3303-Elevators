package elevators;

import commands.Command;
import stateMachines.ElevatorFSM;

public class Elevator implements Runnable {
	// instance variables
	Motor motor;
	ElevatorFSM fsm;
	
	// state holding variables
	int currentFloor;
	Direction currentDirection;
	int destinationFloor;
	
	
	
	public Elevator(){
		fsm = new ElevatorFSM();
		currentFloor = 0;
		destinationFloor = 0;
		motor = new Motor();
		currentDirection = Direction.IDLE;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	//TODO REMOVE AFTER WORKING, HERE FOR TESTS!
	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}
	
	public int getDestinationFloor() {
		return this.destinationFloor;
	}
	
	public void setDestinationFloor(int floor) {
		this.destinationFloor = floor;
	}	
	
	public Direction getDirection() {
		return this.currentDirection;
	}
	
	public void setDirection(Direction direction) {
		this.currentDirection = direction;
	}
	
	public ElevatorFSM.State getElevatorState(){
		return fsm.getState();
	}
	
	
	
	/*
	 * Wait for the latest command, add synchronize wait
	 */
	public void onCommand(Command cmd) {
		ElevatorFSM.State s = fsm.nextState(cmd, this);
		switch (s){
		case INIT :
			break;
		case IDLE :
			break;
		case BOARDING:
			//wait for 30s
			break;
		case MOVING:
		case ARRIVING :
			break;
		default:
			break;
		}
	}
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}
