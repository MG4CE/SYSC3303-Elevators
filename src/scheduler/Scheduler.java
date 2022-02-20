package scheduler;

import java.util.*;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorDispatchCommand;
import commands.ElevatorFloorSensorMessage;
import commands.ElevatorMovingMessage;
import commands.ExternalFloorBtnCommand;
import commands.FloorDirectionLampMessage;
import commands.InteriorElevatorBtnCommand;
import elevators.Direction;
import elevators.Elevator;
import floors.FloorSubsystem;

/**
 * The Scheduler class which is responsible for getting handling the button presses
 * and telling the elevator which floor it needs to travel to. It will also prioritize
 * which floor to visit based on the current position and direction the elevator is moving in.
 */
public class Scheduler implements Runnable {
	public enum controlState{WAIT, DISPATCH};

	//The current state of the scheduler
	private controlState currentState;
	
	// Store the floors elevator must visit, from highest to lowest priority on an up journey
	private ArrayList<Integer> elevatorUpDestinations = new ArrayList<Integer>();
	// Store the floors elevator must visit, from highest to lowest priority on a down journey
	private ArrayList<Integer> elevatorDownDestinations = new ArrayList<Integer>();
	// Store the floors elevator must visit, from highest to lowest priority on this journey, regardless of direction
	private ArrayList<Integer> currentElevatorDestinations = new ArrayList<Integer>();

	//The current floor of the elevator
	private int elevatorCurrentFloor;

	//The direction the elevator is traveling in
	private Direction elevatorCurrentDirection;

	//The most recent command
	private Command latestCommand;

	//If we are ready for a command, or still handling something
	private Boolean readyForCommand;

	//If the program is running
	private Boolean running;

	//An instance of the elevator
	private Elevator elevator;

	private FloorSubsystem floorSubsystem;
	
/**
 * The constructor for the Scheduler class, set up all the necessary variables
 */
	public Scheduler() {
		currentState = controlState.WAIT;
		readyForCommand = true;
		running = true;
		elevatorCurrentFloor = 0;
		elevatorCurrentDirection = Direction.IDLE;
	}


	
	public void setFloorSubSystem(FloorSubsystem fb) {
		this.floorSubsystem = fb;
	}
	
/**
 * Set the elevator for the scheduler
 * @param elevator The elevator
 */
	public void setElevator(Elevator elevator) {
		this.elevator = elevator;
	}

	/**
	 * The run method that will execute when the thread is started
	 * It is responsible for updating the commands that will be sent to the elevator
	 * based on the position and direction
	 */
	@Override
	public void run() {
		while(running) {
			updateControlFSM(schedulerGetCommand());
		}
	}

	/**
	 * Get the next floor the elevator will travel to based on array that holds the list of floor to visit
	 * @return the next floor
	 */
	private int getNextFloor() {
		//The current floors we need to visit has been furfill, we must be changing direction
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

	/**
	 * Determine the order for all the floors to visit based on the appropriate elevator algorithm
	 * @param floor The floor where an elevator was requested
	 * @param direction The direction the person on said floor wants to travel in
	 */
	private void insertNewDestination(int floor, Direction direction) {
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

	private void sendElevatorRequest(int destFloor, int requestId) {
		ElevatorDispatchCommand cmd = new ElevatorDispatchCommand(destFloor, requestId);
		elevator.elevatorPutCommand(cmd);
	}

	/**
	 * Understand a command that came from the floor subsystem on the requests made for the elevator
	 * @param command The command
	 */
	public synchronized void schedulerPutCommand(Command command) {
		while(!readyForCommand) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.print("Failure");
			}
		}
		latestCommand = command;
		readyForCommand = false;
		notifyAll();
	}

	
	/**
	 * Function to hopefully reply correctly
	 * @param command
	 * @param e
	 */
	private void sendFloorSubsystemReply(Command command,Elevator e) {
		this.floorSubsystem.putMessage((ElevatorArrivedMessage)command,e);
	}
	
/**
 * Get the latest command
 * @return The latest command
 */
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


	/**
	 * Update the finite state machine with the current command
	 * @param command The command for the state machine
	 */
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
				sendElevatorRequest(getNextFloor(), ((InteriorElevatorBtnCommand)command).getRequestID());
			}
			if(command instanceof ExternalFloorBtnCommand) {
				ExternalFloorBtnCommand c = (ExternalFloorBtnCommand)command;
				insertNewDestination(c.getFloor(), c.getDirection());
				System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
				currentState = controlState.DISPATCH;
				sendElevatorRequest(getNextFloor(), ((ExternalFloorBtnCommand)command).getRequestID());
			}
			break;
		
		case DISPATCH:
			if(command instanceof ElevatorArrivedMessage) {
				this.sendFloorSubsystemReply(command, this.elevator);
				currentElevatorDestinations.remove(0);
				if(currentElevatorDestinations.isEmpty()) {
					currentState = controlState.WAIT;
				}else { // more floors to visit?
					System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
					sendElevatorRequest(getNextFloor(),((ElevatorArrivedMessage)command).getElevatorID());
					currentState = controlState.DISPATCH;
				}
			}
			else if(command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				insertNewDestination(c.getFloor(), elevatorCurrentDirection);
				System.out.printf("Elevator dispatched to floor %d\n", getNextFloor());
				sendElevatorRequest(getNextFloor(), ((InteriorElevatorBtnCommand)command).getRequestID());
			}
			break;
		}
	}
}
