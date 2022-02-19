package scheduler;

import java.util.*;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorDispatchCommand;
import commands.ElevatorFloorSensorMessage;
import commands.ElevatorMovingMessage;
import commands.ExternalFloorBtnCommand;
import commands.InteriorElevatorBtnCommand;
import elevators.Direction;
import elevators.Elevator;

public class Scheduler implements Runnable {
	public enum controlState{WAIT, DISPATCH};
	controlState currentState;
	
	// Store the floors elevator must visit, from highest to lowest priority
	ArrayList<Integer> elevatorUpDestinations = new ArrayList<Integer>();
	ArrayList<Integer> elevatorDownDestinations = new ArrayList<Integer>();
	ArrayList<Integer> currentElevatorDestinations = new ArrayList<Integer>();

	int elevatorCurrentFloor = 0;
	Direction elevatorCurrentDirection = null;

	Command latestCommand;
	Boolean readyForCommand;
	Boolean running;
	
	Elevator elevator;
	
	public Scheduler() {
		currentState = controlState.WAIT;
		readyForCommand = true;
		running = true;
	}
	
	public void setElevator(Elevator elevator) {
		this.elevator = elevator;
	}
	
	@Override
	public void run() {
		while(running) {
			updateControlFSM(schedulerGetCommand());
		}
	}

	int getNextFloor() {
		if(currentElevatorDestinations.isEmpty())
		{			
			if(elevatorCurrentDirection.equals(Direction.UP))
			{
				currentElevatorDestinations = elevatorDownDestinations;
				return currentElevatorDestinations.get(0);
			}
			else
			{
				currentElevatorDestinations = elevatorUpDestinations;
				return currentElevatorDestinations.get(0);
			}
		}
		else{
			return currentElevatorDestinations.get(0);
		}
	}
	
	void insertNewDestination(int floor, Direction direction) {
		if(elevatorCurrentDirection == Direction.UP) {
			//If the request came from above the current elevator position
			if (floor > elevatorCurrentFloor) {
				//If the request from the person on the floor was to go up
				if (direction.equals(Direction.UP)) {

					if(currentElevatorDestinations.contains(floor))
					{
						return;
					}
					else {
						currentElevatorDestinations.add(floor);
						Collections.sort(currentElevatorDestinations);
					}

				}
				//If the request from the person on the floor was to go down
				else {
					if(elevatorDownDestinations.contains(floor))
					{
						return;
					}
					else
					{
						elevatorDownDestinations.add(floor);
						Collections.sort(elevatorDownDestinations);
						Collections.reverse(elevatorDownDestinations);
					}

				}
			}
			//If the request from the person on the floor was below the current position
			else {
				//If the request from the person on the floor was to go up
				if (direction.equals(Direction.UP)) {

					if(elevatorUpDestinations.contains(floor))
					{
						return;
					}
					else {
						elevatorUpDestinations.add(floor);
						Collections.sort(elevatorUpDestinations);
					}
				}
				//If the request from the person on the floor was to go down
				else {
					if(elevatorDownDestinations.contains(floor))
					{
						return;
					}
					else
					{
						elevatorDownDestinations.add(floor);
						Collections.reverse(elevatorDownDestinations);
					}
				}
			}

		}
		//The elevator is traveling downwards
		else
		{
			//If the request came from above the current elevator position
			if (floor > elevatorCurrentFloor) {
				//If the request from the person on the floor was to go up
				if (direction.equals(Direction.UP)) {
					if(elevatorUpDestinations.contains(floor))
					{
						return;
					}
					else {
						elevatorUpDestinations.add(floor);
						Collections.sort(elevatorUpDestinations);
					}
				}
				//If the request from the person on the floor was to go down
				else {
					if(elevatorDownDestinations.contains(floor))
					{
						return;
					}
					else
					{
						elevatorDownDestinations.add(floor);
						Collections.reverse(elevatorDownDestinations);
					}
				}
			}
			//If the request from the person on the floor was below the current position
			else {
				//If the request from the person on the floor was to go up
				if (direction.equals(Direction.UP)) {
					if(elevatorUpDestinations.contains(floor))
					{
						return;
					}
					else {
						elevatorUpDestinations.add(floor);
						Collections.sort(elevatorUpDestinations);
					}
				}
				//If the request from the person on the floor was to go down
				else {
					if(currentElevatorDestinations.contains(floor))
					{
						return;
					}
					else {
						currentElevatorDestinations.add(floor);
						Collections.sort(currentElevatorDestinations);
					}
				}
			}
		}

	}
	
	private void sendElevatorRequest(int destFloor) {
		ElevatorDispatchCommand cmd = new ElevatorDispatchCommand(destFloor);
		elevator.elevatorPutCommand(cmd);
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
		// On Entry for ALL states, elevator passes a floor
		if(command instanceof ElevatorFloorSensorMessage) {
			ElevatorFloorSensorMessage c = (ElevatorFloorSensorMessage)command;
			elevatorCurrentFloor = c.getFloor();
		}
		// On entry for ALL states, elevator starting!
		if (command instanceof ElevatorMovingMessage) {
			ElevatorMovingMessage c = (ElevatorMovingMessage)command;
			elevatorCurrentDirection = c.getDirection();
		}
		switch (currentState) {
		case WAIT:
			// On new floor request, move to dispatching
			if(command instanceof InteriorElevatorBtnCommand) {
				// add the command
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand)command;
				Direction dir = (elevatorCurrentFloor < c.getFloor()) ? Direction.UP : Direction.DOWN;
				insertNewDestination(c.getFloor(), dir);
				currentState = controlState.DISPATCH;
			}
			if(command instanceof ExternalFloorBtnCommand) {
				ExternalFloorBtnCommand c = (ExternalFloorBtnCommand)command;
				insertNewDestination(c.getFloor(), c.getDirection());
				currentState = controlState.DISPATCH;
			}
			break;
		
		case DISPATCH:
			//entry condition, send request for next destination
			sendElevatorRequest(getNextFloor());
			// Check if elevator has arrived
			if(command instanceof ElevatorArrivedMessage) {
				// remove visited floor
				currentElevatorDestinations.remove(0);
				// no more floors to visit?
				if(currentElevatorDestinations.isEmpty()) {
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
