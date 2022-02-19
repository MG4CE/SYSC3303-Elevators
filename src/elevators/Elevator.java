package elevators;

import java.util.ArrayList;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorDispatchCommand;
import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import commands.MotorMessage;
import components.DirectionLamp;
import elevators.Motor.MotorState;
import scheduler.Scheduler;
import commands.ElevatorFloorSensorMessage;


/**
 * 
 * @author kevin
 *
 */
public class Elevator implements Runnable {
	// FSM State Variables
	public enum State{IDLE, BOARDING, MOVING, ARRIVING};
	
	State currentState;
	
	
	final int NUM_FLOORS = 7;
	final int ELEVATOR_ID;
	
	// Shared Command Variable
	Command latestCommand;
	Boolean readyForCommand;
	Scheduler schedulator;
	
	// Elevator instance variables
	Motor motor;
	Door elevatorDoor;
	DirectionLamp elevatorDirectionLamp;
	
	ArrayList<ElevatorButton> floorButtons;
	ArrayList<ArrivalSensor> sensors;
	ArrayList<ElevatorButtonLamp> floorButtonLamps;
	
	// Elevator fields
	Boolean running;
	int currentFloor;
	Direction currentDirection;
	int destinationFloor;

	/**
	 * Elevator Initializer
	 * @param s the reference to scheduler
	 * @param elevatorId the id of the elevator
	 */
	public Elevator(Scheduler s, int elevatorId){
		ELEVATOR_ID = elevatorId;
		this.currentState = State.IDLE;
		currentFloor = 0;
		destinationFloor = 0;
		motor = new Motor();
		currentDirection = Direction.IDLE;
		latestCommand = null;
		readyForCommand = true;
		running = true;
		
		elevatorDirectionLamp = new DirectionLamp(currentDirection);
		
		elevatorDoor = new Door();
		elevatorDoor.closeDoor();
		
		//Add sensors lists
		sensors = new ArrayList<>();
		floorButtons = new ArrayList<>();
		floorButtonLamps = new ArrayList<>();
		
		//Initialize all the sensors in the lists
		for(int i =0; i<NUM_FLOORS; i++) {
			sensors.add(new ArrivalSensor(i));
			floorButtons.add(new ElevatorButton(i, ELEVATOR_ID));
			floorButtonLamps.add(new ElevatorButtonLamp(i));
		}
		//Scheduler to respond to
		schedulator = s;
		
	}
	
	/**
	 * Click an internal button, will send to the scheduler
	 * @param button between 0 and number of floors
	 */
	public void pushButton(int button) {
		if(button >= NUM_FLOORS) {
			return;
		}
		schedulator.schedulerPutCommand(floorButtons.get(button).pushButton());
		elevatorPutCommand(floorButtons.get(button).pushButton());
	}
	
	
	/**
	 * Getter for current state
	 * @return State currentState
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	/**
	 * Getters for current floor
	 * @param height of elevator
	 * @return int
	 */
	private int getCurrentFloor(int height) {
		for(ArrivalSensor sensor : sensors) {
			if(height == sensor.getLocation()) {
				return sensor.getFloor();
			}
		}
		return 0;
	}
	
	/**
	 * Setter for current Destination floor
	 * @param floor int
	 */
	private void setDestinationFloor(int floor) {
		destinationFloor = floor;
	}	
	
	/**
	 * Getter for the elevators current moving direction
	 * @return Direction
	 */
	public Direction getDirection() {
		return currentDirection;
	}
	
	/**
	 * Setter for the elevators current Direction
	 * @param direction
	 */
	public void setDirection(Direction direction) {
		currentDirection = direction;
	}
	
	/**
	 * Ferry response commands to the scheduler
	 * Can be a ElevatorMovingMessage or ElevatorArrivedMessage
	 * 
	 * @param response COmmand
	 */
	private void notifySchedulerOfState(Command response) {
		this.schedulator.schedulerPutCommand(response);
	}
	
	// FSM Shit
	@Override
	public void run() {
		while(running) {
			updateFSM(getLatestCommand());
		}
	}
	
	/**
	 * Put commands to the elevator to handle
	 * Comes from
	 * Scheduler - ElevatorDispatchCommand
	 * Motor - ElevatorFloorSensorMessage
	 * @param command
	 */
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
	
	/**
	 * This elevator consuming the new command or wait if not set
	 * @return
	 */
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
		if (command instanceof MotorMessage) {
			MotorMessage c = (MotorMessage)command;
			currentFloor = getCurrentFloor(c.getHeight());
		}
		
		switch (this.currentState){
		case IDLE :
			if(command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				System.out.printf("Received command to go to floor %d\n", c.getDestFloor());
				if(this.currentFloor == c.getDestFloor()) {
					this.currentState = State.BOARDING;
					return;
				}else {
					currentDirection = (currentFloor < c.getDestFloor()) ? Direction.UP : Direction.DOWN;
					motor.move(currentDirection); // Transition action
					currentState = State.MOVING;
					notifySchedulerOfState(new ElevatorMovingMessage(ELEVATOR_ID, c.getDestFloor(), currentDirection));
				}
			}
			break;
			
		case BOARDING:
			elevatorDoor.openDoor();
			elevatorDoor.closeDoor();
			// check if button on other floor was clicked
			if (command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand)command;
				if (c.getFloor() == currentFloor) {
					currentState = State.BOARDING;
				}else {
					currentState = State.IDLE;
				}
				System.out.printf("Interrior button %d clicked!\n", c.getFloor());
				this.elevatorDirectionLamp.turnOffLight();
				this.currentState = State.IDLE;
			}
			break;
		
		case MOVING:
			// check if a higher priority stop was sent by the scheduler, back to MOVING
			if (command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				setDestinationFloor(c.getDestFloor()); // update latest destination
				currentState = State.MOVING;
			}
			break;
			
		case ARRIVING :
			if(command instanceof ElevatorFloorSensorMessage) {
				motor.stopMotor();
				currentState = State.BOARDING;
				notifySchedulerOfState(new ElevatorArrivedMessage(ELEVATOR_ID, currentFloor));
			}
			break;
		}		
	}	
}
