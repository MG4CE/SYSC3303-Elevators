package scheduler;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

import elevatorCommands.Direction;

public class Elevator {
	
	public enum ElevatorState {
		MOVING,
		STOPPED
	}
	
	private int port;
	private InetAddress address;
	private int currentFloor;
	private int elevatorID;
	private ArrayList<ElevatorRequest> floorDestinations;
	private Direction currentDirection;
	private ElevatorState state;
	
	public Elevator(int port, int elevatorID, int currentFloor, InetAddress address) {
		this.port = port;
		this.address = address;
		this.elevatorID = elevatorID;
		this.currentFloor = currentFloor;
		this.floorDestinations = new ArrayList<>();
		this.currentDirection = Direction.UP;
		this.state = ElevatorState.STOPPED;
	}
	
	//TODO: Incorporate the direction of the request as a factor when scheduling destinations
	public void addDestination(ElevatorRequest req) {		
		Direction direction = currentDirection;
		ElevatorRequest cur_track;
		ArrayList<ElevatorRequest> left = new ArrayList<>();
		ArrayList<ElevatorRequest> right = new ArrayList<>();
		ArrayList<ElevatorRequest> seek_sequence = new ArrayList<>();
	    
		floorDestinations.add(req);
	    
	    for (int i = 0; i < floorDestinations.size(); i++) {
	    	ElevatorRequest f = floorDestinations.get(i);
	        if (f.getFloor() < currentFloor) {
	            left.add(f);
	        } else if (f.getFloor() > currentFloor) {
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
	        else if (direction == Direction.UP) {
	            for (int i = 0; i < right.size(); i++) {
	                cur_track = right.get(i);
	                seek_sequence.add(cur_track);
	            }
	            direction = Direction.DOWN;
	        }
	    }
	    floorDestinations = seek_sequence;
	}
	
	public ElevatorState getState() {
		return this.state;
	}
	
	public void setState(ElevatorState state) {
		this.state = state;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public int getElevatorID() {
		return this.elevatorID;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	public Direction getlDirection() {
		return this.currentDirection;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public ArrayList<ElevatorRequest> getFloorDestinations() {
		return floorDestinations;
	}
	
	public ElevatorRequest popTopRequest() {
		ElevatorRequest r = floorDestinations.remove(0);
		if(r.getFloor() < currentFloor) {
			currentDirection = Direction.DOWN;
		} else if (r.getFloor() > currentFloor) {
			currentDirection = Direction.UP;
		}
		return r;
	}

	public void switchDirections() {
		if (currentDirection == Direction.DOWN) {
			currentDirection = Direction.UP;
		} else if (currentDirection == Direction.UP) {
			currentDirection = Direction.DOWN;
		}
	}
	
	public int getNumDestinations() {
		return this.floorDestinations.size();
	}
	
	public InetAddress getAddress() {
		return this.address;
	}
	

	public ElevatorRequest peekTopRequest() {
		return this.floorDestinations.get(0);
	}
	
	public static void main(String[] args) {
	    Elevator e = new Elevator(123, 1, 4, null);
	    e.addDestination(new ElevatorRequest(1, 999, Direction.UP));
	    e.addDestination(new ElevatorRequest(2, 999, Direction.DOWN));
	    e.addDestination(new ElevatorRequest(3, 999, Direction.DOWN));
	    e.addDestination(new ElevatorRequest(5, 999, Direction.UP));
	    e.addDestination(new ElevatorRequest(6, 999, Direction.UP));
	    e.addDestination(new ElevatorRequest(7, 999, Direction.DOWN));
	    e.addDestination(new ElevatorRequest(8, 999, Direction.DOWN));
	    System.out.print("Sequence is" + "\n");
	    for (int i = 0; i < e.floorDestinations.size(); i++) {
	        System.out.print(e.floorDestinations.get(i).getFloor() + "\n");
	    }
	}
}

