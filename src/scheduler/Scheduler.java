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
	
	private controlState currentState;
	
	// Store the floors elevator must visit, from highest to lowest priority
	private ArrayList<Integer> elevatorUpDestinations = new ArrayList<Integer>();
	private ArrayList<Integer> elevatorDownDestinations = new ArrayList<Integer>();
	private ArrayList<Integer> currentElevatorDestinations = new ArrayList<Integer>();
	
	private int elevatorCurrentFloor = 0;
	private Direction elevatorCurrentDirection = Direction.IDLE;

	private Command latestCommand;
	private Boolean readyForCommand;
	private Boolean running;
	
	private Elevator elevator;
	
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
			System.out.println("Elevator starting");
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
				System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
				currentState = controlState.DISPATCH;
				sendElevatorRequest(getNextFloor());
			}
			if(command instanceof ExternalFloorBtnCommand) {
				ExternalFloorBtnCommand c = (ExternalFloorBtnCommand)command;
				insertNewDestination(c.getFloor(), c.getDirection());
				System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
				currentState = controlState.DISPATCH;
				sendElevatorRequest(getNextFloor());
			}
			break;
		
		case DISPATCH:
			if(command instanceof ElevatorArrivedMessage) {
				currentElevatorDestinations.remove(0);
				if(currentElevatorDestinations.isEmpty()) {
					currentState = controlState.WAIT;
				}else { // more floors to visit?
					System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
					sendElevatorRequest(getNextFloor());
					currentState = controlState.DISPATCH;
				}
			}
			else if(command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				insertNewDestination(c.getFloor(), elevatorCurrentDirection);
				System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
				sendElevatorRequest(getNextFloor());
			}
			break;
		}
	}
}
