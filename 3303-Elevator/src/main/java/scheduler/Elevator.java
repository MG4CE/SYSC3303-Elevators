package scheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import elevatorCommands.Button;
import elevatorCommands.Direction;

/**
 * The ElevatorControl class is a simulation of the elevator
 * that can be used by the scheduler to send the appropriate commands to
 * the elevator.
 */
public class Elevator {
	
	private static final int BUTTON_WAIT_TIMEOUT = 1000;
	private static final int TIMEOUT = 10000;

	/**
	 * The enum for the different states the elevator could have
	 */
	public enum ElevatorState {
		MOVING,
		STOPPED,
		DOOR_FAULT,
		TIMEOUT
	}
		
	//The port of the elevator
	private int port;
	//The address for our UDP communication
	private InetAddress address;
	//The current floor of the elevator
	private int currentFloor;
	//The ID of the elevator
	private int elevatorID;
	//The destination floors the elevator has to service
	private ArrayList<ElevatorRequest> floorDestinations;
	//The current direction the elevator is traveling in
	private Direction currentDirection;
	//The state the elevator currently has
	private ElevatorState state;
	//The current destination of the floor
	private int currentDestinationFloor;
	private Scheduler scheduler;
	private Boolean isTimerOff;
	private Boolean isTimeoutTimerOff;
	private Timer interiorButtonTimer;
	private TimerTask interiorButtonTimerTask;
	private Timer timeoutTimer;
	private TimerTask timeoutTimerTask;
	private Timer doorFaultSimTimer;
	private TimerTask doorFaultSimTimerTask;
	private int doorFaultSimTimeout;
	private int timeoutTimerCancelCounter; 
	private int interiorButtonTimerCancelCounter; 

	/**
	 * The constructor for the elevator control class
	 * @param port The port of the elevator
	 * @param elevatorID The ID of the elevator
	 * @param currentFloor The current floor the elevator is on
	 * @param address The internet address
	 */
	public Elevator(int port, int elevatorID, int currentFloor, InetAddress address, Scheduler scheduler) {
		this.port = port;
		this.address = address;
		this.elevatorID = elevatorID;
		this.currentFloor = currentFloor;
		this.floorDestinations = new ArrayList<>();
		this.currentDirection = Direction.STATIONARY;
		this.state = ElevatorState.STOPPED;
		this.currentDestinationFloor = -1;
		this.scheduler = scheduler;
		this.isTimerOff = true;
		this.interiorButtonTimer = null;
		this.interiorButtonTimerTask = null;
		this.timeoutTimer = null;
		this.timeoutTimerTask = null;
		this.doorFaultSimTimer = null;
		this.doorFaultSimTimerTask = null;
		this.isTimeoutTimerOff = true;
		this.timeoutTimerCancelCounter = 0;
		this.interiorButtonTimerCancelCounter = 0;
	}

	/**
	 * Add a destination to the elevators list of destinations
	 * @param req the ElevatorRequest that will hold the destination
	 */
	public void addDestination(ElevatorRequest req) {		
		ElevatorRequest cur_track;
		ArrayList<ElevatorRequest> left = new ArrayList<>();
		ArrayList<ElevatorRequest> right = new ArrayList<>();
		ArrayList<ElevatorRequest> seek_sequence = new ArrayList<>();
	    
		if (req != null) {
			floorDestinations.add(req);
			
		    if (currentDestinationFloor > req.getFloor() && currentDirection == Direction.STATIONARY) {
		    	this.currentDirection = Direction.DOWN;
		    } else if (currentDestinationFloor <= req.getFloor() && currentDirection == Direction.STATIONARY) {
		    	this.currentDirection = Direction.UP;
		    }
		}
	    
		Direction direction = currentDirection;
	    
	    for (int i = 0; i < floorDestinations.size(); i++) {
	    	ElevatorRequest f = floorDestinations.get(i);
	        if (f.getFloor() < currentFloor && direction == Direction.DOWN) {
	            left.add(f);
	        } else if (f.getFloor() > currentFloor && direction == Direction.UP) {
	        	right.add(f);
	        } else if (f.getFloor() > currentFloor && direction == Direction.DOWN) {
	        	left.add(f);
	    	} else if (f.getFloor() < currentFloor && direction == Direction.UP) {
	    		right.add(f);
	    	} else if (f.getFloor() == currentFloor) {
	        	right.add(f);
	        }
	    }
	 
	    Collections.sort(left);
	    Collections.sort(right);
	 
	    int run = 2;
	    while (run-- > 0) {
	        if (direction == Direction.DOWN) {
	            for (int i = left.size() - 1; i >= 0; i--) {
	                cur_track = left.get(i);
	 	            seek_sequence.add(cur_track);
	            }
	            direction = Direction.UP;
	        }
	        else if (direction == Direction.UP || currentFloor < req.getFloor()) {
	            for (int i = 0; i < right.size(); i++) {
	                cur_track = right.get(i);
	                seek_sequence.add(cur_track);
	            }
	            direction = Direction.DOWN;
	        }
	    }
	    
	    floorDestinations = seek_sequence;
//	    System.out.println("Current direction: " + currentDirection);
//	    System.out.print("Sequence is" + "\n");
//	    for (int i = 0; i < floorDestinations.size(); i++) {
//	        System.out.print(floorDestinations.get(i).getFloor() + "\n");
//	    }
	}

	/**
	 * Get the state of the elevator
	 * @return the state
	 */
	public ElevatorState getState() {
		return this.state;
	}

	/**
	 * Set the state of the elevator
	 * @param state the state to be set
	 */
	public void setState(ElevatorState state) {
		this.state = state;
	}

	/**
	 * Get the port of the elevator
	 * @return the elevator port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * get the elevator ID
	 * @return the Id of the elevator
	 */
	public int getElevatorID() {
		return this.elevatorID;
	}

	/**
	 * Get the current floor of the elevator
	 * @return the current floor
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}


	/**
	 * Get the last directon of the elevator
	 * @return the last direction
	 */
	public Direction getlDirection() {
		return this.currentDirection;
	}

	/**
	 * Set the current floor of the elevator
	 * @param currentFloor the floor to be set
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	/**
	 * get all the destinations
	 * @return The floor destinations
	 */
	public ArrayList<ElevatorRequest> getFloorDestinations() {
		return floorDestinations;
	}

	/**
	 * Pop the top request off of the destinations
	 * @return the top destination
	 */
	public ElevatorRequest popTopRequest() {
		ElevatorRequest r = floorDestinations.remove(0);
		if (r.getFloor() < currentFloor) {
			currentDirection = Direction.DOWN;
		} else if (r.getFloor() > currentFloor) {
			currentDirection = Direction.UP;
		}
		return r;
	}

	/**
	 * Switch the direction the elevator is traveling in
	 */
	public void switchDirections() {
		if (currentDirection == Direction.DOWN) {
			currentDirection = Direction.UP;
		} else if (currentDirection == Direction.UP) {
			currentDirection = Direction.DOWN;
		}
		addDestination(null);
		currentDestinationFloor = peekTopRequest().getFloor();
	}

	/**
	 * get the number of destinations
	 * @return the number of destinations
	 */
	public int getNumDestinations() {
		return this.floorDestinations.size();
	}

	/**
	 * Get the InetAddress
	 * @return The InetAddress
	 */
	public InetAddress getAddress() {
		return this.address;
	}


	/**
	 * Peek the top request
	 * @return the top request
	 */
	public ElevatorRequest peekTopRequest() {
		if(floorDestinations.size() == 0) {
			return null;
		}
		return this.floorDestinations.get(0);
	}

	/**
	 * Set the current destination of the elevator control
	 * @param destination the elevator control destination
	 */
	public void setCurrentDestination(int destination) {
		if (currentDestinationFloor > destination && currentDirection == Direction.UP) {
			switchDirections();
		} else if (currentDestinationFloor < destination && currentDirection == Direction.DOWN) {
			switchDirections();
		} else {
			this.currentDestinationFloor = destination;
		}
	}

	/**
	 * get the current floor destination
	 * @return the floor destination
	 */
	public int getCurrentDestination() {
		return this.currentDestinationFloor;
	}
	
	public int isRequestInQueue(ElevatorRequest request) {
		for (ElevatorRequest r : floorDestinations) {
			if (request.getFloor() == r.getFloor()) {
				return 1;
			}
		}
		return 0;
	}
	
	public void startWaitTimer() {
		interiorButtonTimer = new Timer();
		interiorButtonTimerTask = makeTimerTask();
		interiorButtonTimer.schedule(interiorButtonTimerTask, BUTTON_WAIT_TIMEOUT);
		isTimerOff = false;
	}
	
	public void stopWaitTimer() {
		if (!isTimerOff) {
			interiorButtonTimer.cancel();
			interiorButtonTimerCancelCounter++; //janky way of stopping timers from running
			isTimerOff = true;
		}
	}
	
	private TimerTask makeTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				if(interiorButtonTimerCancelCounter == 0) {
					Scheduler.LOGGER.warn("Floorsubsystem failed to give internal button press!");
					if (peekTopRequest() != null ) {
						try {
							scheduler.sendSchedulerDispatchMessage(peekTopRequest().getFloor(), port, currentDirection, peekTopRequest().getRequestID(), elevatorID, address);
						} catch (IOException e) {
							e.printStackTrace();
						}
						setCurrentDestination(peekTopRequest().getFloor());
					} else {
						stopTimeoutTimer();
					}
				} else {
					interiorButtonTimerCancelCounter--;
				}
			}
        };
	}
	
	public void startTimeoutTimer() {
		System.out.println("Elevator " + elevatorID + " timeout timer started");
		timeoutTimer = new Timer();
		timeoutTimerTask = makeTimeoutTask();
		timeoutTimer.schedule(timeoutTimerTask, TIMEOUT);
		isTimeoutTimerOff = false;
	}
	
	public void resetTimeoutTimer() {
		timeoutTimer.cancel();
		timeoutTimer = new Timer();
		timeoutTimerTask = makeTimeoutTask();
		timeoutTimer.schedule(timeoutTimerTask, TIMEOUT);
	}
	
	public void stopTimeoutTimer() {
		if (!isTimeoutTimerOff) {
			timeoutTimer.cancel();
			timeoutTimerCancelCounter++;
			isTimeoutTimerOff = true;
		}
	}
	
	private TimerTask makeTimeoutTask() {
		Elevator e = this;
		return new TimerTask() {
			@Override
			public void run() {
				System.out.println("Elevator " + elevatorID + " " + timeoutTimerCancelCounter);
				if(timeoutTimerCancelCounter == 0) {
					state = ElevatorState.TIMEOUT;
					scheduler.hardFaultElevator(e);
				} else {
					timeoutTimerCancelCounter--;
				}
			}
        };
	}
	
	public void createDoorFaultSimTimer(int timeout) {
		doorFaultSimTimer = new Timer();
		doorFaultSimTimerTask = makeDoorFaultSimTask();
		doorFaultSimTimeout = timeout;
	}
	
	public void startDoorFaultSimTimer() {
		doorFaultSimTimer.schedule(doorFaultSimTimerTask, doorFaultSimTimeout);
	}
	
	private TimerTask makeDoorFaultSimTask() {
		return new TimerTask() {
			@Override
			public void run() {
				Scheduler.LOGGER.info("Ending simulated door fault for Elevator " + elevatorID);
				try {
					scheduler.sendStopDoorFaultSimulateFaultMessage(elevatorID, port, address);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        };
	}
	
	public ArrayList<ElevatorRequest> getAllExternalRequest() {
		ArrayList<ElevatorRequest> ret = new ArrayList<>();
		for (ElevatorRequest r : floorDestinations) {
			if (r.getRequestType() == Button.EXTERIOR) {
				ret.add(r);
			}
		}
		return ret;
	}
	
	public Boolean isSchedulable() {
		if (state == ElevatorState.MOVING || state == ElevatorState.STOPPED) {
			return true;
		}
		return false;
	}
	
	public void increaseSameFloorPriority() {
	    int shift = 0;  
	    for (int i = 0; i < floorDestinations.size(); i++) {
	    	if (floorDestinations.get(i).getFloor() == currentDestinationFloor && currentDestinationFloor == currentFloor) {
	    		ElevatorRequest r = floorDestinations.remove(i);
	    		floorDestinations.add(shift, r);
	    		shift++;
	    	}
	    }
	}
	
	public Boolean isTimeoutTimerOff() {
		return isTimeoutTimerOff;
	}
	
	public static void main(String[] args) {
	    Elevator e = new Elevator(123, 1, 1, null, null);
	    e.currentFloor = 1;
	    e.currentDestinationFloor = 1;
	    e.currentDirection = Direction.UP;
	    e.addDestination(new ElevatorRequest(10, 999, Direction.UP, Button.EXTERIOR));
	    e.addDestination(new ElevatorRequest(10, 999, Direction.UP, Button.EXTERIOR));
	    e.addDestination(new ElevatorRequest(3, 999, Direction.UP, Button.EXTERIOR));
	    e.addDestination(new ElevatorRequest(5, 999, Direction.UP, Button.EXTERIOR));
	    e.addDestination(new ElevatorRequest(6, 999, Direction.UP, Button.EXTERIOR));
	    e.addDestination(new ElevatorRequest(7, 999, Direction.DOWN, Button.EXTERIOR));
	    e.addDestination(new ElevatorRequest(8, 999, Direction.DOWN, Button.EXTERIOR));
	    System.out.print("Sequence is" + "\n");
	    for (int i = 0; i < e.floorDestinations.size(); i++) {
	        System.out.print(e.floorDestinations.get(i).getFloor() + "\n");
	    }
	}
}

