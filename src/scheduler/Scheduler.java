package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.InteriorElevatorBtnCommand;
import elevators.Direction;
import elevators.Elevator;

public class Scheduler {
	public enum controlState{WAIT, DISPATCH};
	controlState currentState;
	
	// Store the floors elevator must visit, from highest to lowest priority
	ArrayList<Integer> elevatorDestinations;
	
	Command latestCommand;
	Boolean readyForCommand;
	
	Elevator elevator;
	
	public Scheduler(Elevator elevator) {
		currentState = controlState.WAIT;
		readyForCommand = true;
		this.elevator = elevator;
	}

	int getNextFloor() {
		// TODO GET NEXT FLOOR BASED OF DIR
		return elevatorDestinations.get(0);
	
	}
	
	void insertNewDestination(int floor) {
		if(elevatorDestinations.contains(floor)) {
			return;
		}
		elevatorDestinations.add(floor);
		Collections.sort(elevatorDestinations);
	}
	
	private void sendElevatorRequest(int destFloor) {
		//TODO REQUEST ID???
		InteriorElevatorBtnCommand cmd = new InteriorElevatorBtnCommand(destFloor, 0);
	}
	
	public synchronized void schedulerPutCommand(Command command) {
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
	
	private synchronized Command schedulerGetCommand() {
		while(readyForCommand) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.print("handle me!");
			}
		}
		readyForCommand = true;
		notifyAll();
		return latestCommand;
	}
	
	public synchronized void updateControlFSM(Command command) {
		switch (currentState) {
		case WAIT:
			// On new floor request, move to dispatching
			if(command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				currentState = controlState.DISPATCH;
			}
			break;
		
		case DISPATCH:
			//entry condition, send request for next destination
			sendElevatorRequest(getNextFloor());
			// Check if elevator has arrived
			if(command instanceof ElevatorArrivedMessage) {
				// remove visited floor
				elevatorDestinations.remove(elevator.getDestinationFloor());
				// no more floors to visit?
				if(elevatorDestinations.isEmpty()) {
					currentState = controlState.WAIT;
				}else { // more floors to visit?
					currentState = controlState.DISPATCH;
				}
			}
			if(command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
			}
			
			break;
		}
	}
}
