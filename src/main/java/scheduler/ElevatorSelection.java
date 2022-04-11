package scheduler;

import java.util.ArrayList;
import java.util.Iterator;

import message.Direction;

public class ElevatorSelection {
	
	/**
	 * Assign the best elevator for the a request
	 * 
	 * @param req the request
	 * @return the best elevator
	 */
    public static ElevatorControl assignBestElevator(ElevatorRequest req, ArrayList<ElevatorControl> elevators, int numFloors) {
    	if(elevators.size() == 0) {
    		return null;
    	}
    	
    	ElevatorControl selectedElevator = null;
    	if(elevators.size() == 1) {
    		synchronized(elevators.get(0)){
    			elevators.get(0).addDestination(req);
    		}
    		selectedElevator = elevators.get(0);
    	} else {
    		ElevatorControl best = null;
    		Iterator<ElevatorControl> iter = elevators.iterator();
    		while (iter.hasNext()) {
	    		ElevatorControl elevator = iter.next();
	    		if(best != null) {
		    		synchronized(elevator){
		    			synchronized(best){
		    	    		if(compareElevator(elevator, best, req, numFloors)) {
		    	    			best = elevator;
		    	    		}
		    			}
		    		}
	    		} else {
	    			best = elevator;
	    		}
    		}
    		synchronized(best){
    			best.addDestination(req);
    		}
    		selectedElevator = best;
    	}
    	return selectedElevator;
    }

	/**
	 * Compare the elevator requests and their score
	 * 
	 * @param e1 the first elevator
	 * @param e2 the second elevator
	 * @param request the request
	 * @return whether elevator one preferred over two
	 */
	public static Boolean compareElevator(ElevatorControl e1, ElevatorControl e2, ElevatorRequest request, int numFloors) {
    	int e1Score = evaluateDirectionalScore(e1, request, numFloors) - e1.getNumDestinations() + e1.isRequestInQueue(request)*2 + getSchedulableScore(e1);
    	int e2Score = evaluateDirectionalScore(e2, request, numFloors) - e2.getNumDestinations() + e2.isRequestInQueue(request)*2 + getSchedulableScore(e2);
    	if (e1Score > e2Score) {
        	return true;
    	}
    	return false;
    }

	/**
	 * Evaluate the request priority using the direction as a factor
	 * 
	 * @param e the elevator
	 * @param request the new request
	 * @return the score returned
	 */
	public static int evaluateDirectionalScore(ElevatorControl e, ElevatorRequest request, int numFloors) {
    	int score = e.getCurrentFloor() - request.getFloor();
    	Direction direction = e.getlDirection();
    	if (score == 0 && direction != Direction.STATIONARY) {
    		return -9999 + e.getElevatorID();
    	}
    	if (score < 0 && direction == Direction.UP) {
    		score *= -1;
    	} else if (score < 0 && direction == Direction.DOWN) {
    		score = (numFloors - Math.abs(score)) * -1;
    	} else if (score > 0 && direction == Direction.UP) {
    		score = (numFloors - Math.abs(score)) * -1;
    	} else if (direction == Direction.STATIONARY) {
    		return Math.abs(score);
    	}
    	return score;
    }
	

	/**
	 * Returns a score based on if the elevator is schedulable or not
	 * 
	 * @param e Elevator to check
	 * @return Score int
	 */
	public static int getSchedulableScore(ElevatorControl e) {
		if (!e.isSchedulable()) {
			return -99999999;
		}
		return 0;
	}
}
