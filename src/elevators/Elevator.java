package elevators;

import java.util.ArrayList;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorDispatchCommand;
import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import commands.MotorMessage;
import components.DirectionLamp;
import scheduler.Scheduler;
import commands.ElevatorFloorSensorMessage;


/**
 * Elevator Controller
 * This is the new instance of elevator it is a slave to the scheduler class
 * It implements a finite state system and iterates through them based on call from the scheduler
 * 
 * The elevator will wait for commands from the scheduler and send a response back
 */
public class Elevator implements Runnable {
	// FSM State Variables
	public enum State{IDLE, BOARDING, MOVING, ARRIVING};
	
	private State currentState;
	//Max floors and Id of elevator
	private final int NUM_FLOORS = 7;
	private final int ELEVATOR_ID = 1;
	
	// Shared Command Variable
	private Command latestCommand;
	private Boolean readyForCommand;
	private Scheduler schedulator;
	
	private int requestId;
	
	// Elevator instance variables
	private Motor motor;
	private Door elevatorDoor;
	private DirectionLamp elevatorDirectionLamp;
	
	//Arrays of internal components
	private ArrayList<ElevatorButton> floorButtons;
	private ArrayList<ArrivalSensor> sensors;
	private ArrayList<ElevatorButtonLamp> floorButtonLamps;
	
	// Elevator fields
	private Boolean running;
	private volatile int currentFloor;
	private Direction currentDirection;
	private int destinationFloor;

	/**
	 * Elevator Initializer
	 * @param s the reference to scheduler
	 * @param elevatorId the id of the elevator
	 */
	public Elevator(Scheduler s, int elevatorId){
		this.currentState = State.IDLE;
		currentFloor = 0;
		destinationFloor = 0;
		motor = new Motor(this);
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
		//elevatorPutCommand(floorButtons.get(button).pushButton());
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
	
	/**
	 * Run method implemented from Runnable
	 */
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
				System.out.print("invalid");
			}
		}
		readyForCommand = true;
		notifyAll();
		return latestCommand;
	}
			
	/*
	 * FSM IMPLEMETNATION
	 */
	/**
	 * This method is to increment the elevators state and send messages back to the scheduler
	 * @param command
	 */
	public synchronized void updateFSM(Command command) {
		//Motor sending message to say what floor it is
		if (command instanceof MotorMessage) {
			MotorMessage c = (MotorMessage)command;
			System.out.printf("Elevator passing %d\n", c.getHeight());
			currentFloor = c.getHeight();
			schedulator.schedulerPutCommand(new ElevatorFloorSensorMessage(currentFloor, ELEVATOR_ID));
		}
		
		switch (this.currentState){
		case IDLE :
			if(command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				setDestinationFloor(c.getDestFloor());
				if(this.currentFloor == c.getDestFloor()) {
					System.out.println("Elevator Already at Floor, going to boarding");
					this.currentState = State.BOARDING;
					return;
				}else {
					currentDirection = (currentFloor < c.getDestFloor()) ? Direction.UP : Direction.DOWN;
					motor.move(currentDirection); // Transition action
					currentState = State.MOVING;
					this.requestId = ((ElevatorDispatchCommand)command).getRequestId();
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
				this.elevatorDirectionLamp.turnOffLight();
				this.currentState = State.IDLE;
			}else if(command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				if(c.getDestFloor() != currentFloor) {
					destinationFloor = c.getDestFloor();
					currentDirection = (currentFloor > destinationFloor) ? Direction.DOWN : Direction.UP;
					currentState = State.MOVING;
					motor.move(currentDirection);
				}
			}
			break;
		
		case MOVING:
			// check if a higher priority stop was sent by the scheduler, back to MOVING
			if (command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				System.out.printf("Elevator dispatched to floor %d\n", c.getDestFloor());
				setDestinationFloor(c.getDestFloor()); // update latest destination
			} if(command instanceof MotorMessage) {
				if(currentDirection == Direction.UP && currentFloor == (destinationFloor -1)) {
					motor.stopMotor();
					System.out.println("Entering ariving");
					currentState = State.ARRIVING;
				}else if(currentDirection == Direction.DOWN && currentFloor == (destinationFloor +1)) {
					System.out.println("Entering ariving");
					motor.stopMotor();
					currentState = State.ARRIVING;
				}else if(currentFloor == destinationFloor)
                {
                    System.out.println("Entering ariving");
                    motor.stopMotor();
                    currentState = State.ARRIVING;
                }
			}
			break;
			
		case ARRIVING :
			if(command instanceof MotorMessage) {
				System.out.println("Elevator has Arrived!");
				motor.stopMotor();
				currentState = State.BOARDING;
				notifySchedulerOfState(new ElevatorArrivedMessage(ELEVATOR_ID, currentFloor, this.requestId));
			}
			break;
		}
	}	
}
