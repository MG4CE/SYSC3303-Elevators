package elevators;

import java.util.concurrent.TimeoutException;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import commands.ElevatorFloorSensorMessage;


public class Elevator implements Runnable {
	// FSM State Variables
	public enum State{IDLE, BOARDING, MOVING, ARRIVING};
	State currentState;
	
	// Shared Command Variable
	Command latestCommand;
	Boolean readyForCommand;
	
	// Elevator instance variables
	Motor motor;
	
	// Elevator fields
	Boolean running;
	int currentFloor;
	Direction currentDirection;
	int destinationFloor;

	
	public Elevator(){
		this.currentState = State.IDLE;
		currentFloor = 0;
		destinationFloor = 0;
		motor = new Motor();
		currentDirection = Direction.IDLE;
		latestCommand = null;
		readyForCommand = true;
		running = true;
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	public void setDestinationFloor(int floor) {
		destinationFloor = floor;
	}	
	
	public Direction getDirection() {
		return currentDirection;
	}
	
	public void setDirection(Direction direction) {
		currentDirection = direction;
	}

	public void setCurrentFloor(int floor) {
		currentFloor = floor;
	}
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	// FSM Shit
	@Override
	public void run() {
		while(running) {
			updateFSM(getLatestCommand());
		}
	}
	
	public synchronized void elevatorPutCommand(Command command) {
		while(!readyForCommand) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.print("something failed???");
			}
		}
		latestCommand = command;
		readyForCommand = false;
		notifyAll();
	}
	
	private synchronized Command getLatestCommand() {
		while(readyForCommand) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.print("Fuck you");
			}
		}
		readyForCommand = true;
		notifyAll();
		return latestCommand;
	}
			
	/*
	 * FSM IMPLEMETNATION
	 */
	public synchronized void updateFSM(Command command) {
		switch (this.currentState){
		case IDLE :
			if(command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				if(this.currentFloor == c.getFloor()) {
					this.currentState = State.BOARDING;
					return;
				}else {
					currentState = State.MOVING;
				}
			}
			break;
			
		case BOARDING:
			//try {
			//	wait(30 * 1000); //TODO COME BACK
			//} catch (InterruptedException e) {
			//	if (currentState == State.BOARDING) 
			//		this.currentState = State.IDLE;
			//}
			// check if button on other floor was clicked
			if (command instanceof InteriorElevatorBtnCommand) {
				this.currentState = State.IDLE;
			}
			break;
	
		
		case MOVING:
			// check if a higher priority stop was sent by the scheduler, back to MOVING
			if (command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				setDestinationFloor(c.getFloor()); // update latest destination
				currentState = State.MOVING;
			} 
			// Any time elevator passes a floor
			if(command instanceof ElevatorFloorSensorMessage) {
				ElevatorFloorSensorMessage c = (ElevatorFloorSensorMessage) command;
				setCurrentFloor(c.getFloor());
				if(currentDirection == Direction.UP && currentFloor == destinationFloor -1) {
					currentState = State.ARRIVING;
				}else if(currentDirection == Direction.DOWN && currentFloor == destinationFloor +1) {
					currentState = State.ARRIVING;
				}
			}
			break;
			
		case ARRIVING :
			if(command instanceof ElevatorFloorSensorMessage) {
				currentState = State.BOARDING;
			}
			break;
		}		
	}	
}
