package scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SchedulerBean {
	
	
	
	private List<Integer> elevatorList;
	private List<Integer> elevatorAtFloorList;
	private List<Integer> floorRequestedList;
	private List<Integer> numberOccupentsList;
	
	private int[] elevatorArray;
	private int[] elevatorFloorArray;
	private int[] floorRequestedArray;
	private int[] numberOccupentsArray;

	public SchedulerBean() {
		elevatorList = new ArrayList<>();
		elevatorAtFloorList = new ArrayList<>();
		floorRequestedList = new ArrayList<>();
		numberOccupentsList = new ArrayList<>();
	}
	
	public void setElevatorList(List<Integer> elevatorList) {
		this.elevatorList = elevatorList;
	}
	
	public void setElevatorFloorRequest(List<Integer> elevatorFloorList) {
		this.elevatorAtFloorList = elevatorFloorList;
	}
	
	public void setFloorRequestedList(List<Integer> elevatorFloorRequestList) {
		this.floorRequestedList = elevatorFloorRequestList;
	}
	
	public void setNumberOccupentsList(List<Integer> elevatorOccupentsList) {
		this.numberOccupentsList = elevatorOccupentsList;
	}
	
	public void buildArrays() {
		this.elevatorArray = this.elevatorList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
		
		this.elevatorFloorArray = this.elevatorAtFloorList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
		
		this.floorRequestedArray = this.floorRequestedList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
		
		this.numberOccupentsArray = this.numberOccupentsList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
	}
	
	public int[] getElevatorArray() {
		return this.elevatorArray;
	}
	
	public int[] getElevatorFloorArray() {
		return this.elevatorFloorArray;
	}
	
	public int[] getRequestedFloorArray() {
		return this.floorRequestedArray;
	}
	
	public int[] getNumberOccupentsArray() {
		return this.numberOccupentsArray;
	}
	
	public int findElevatorIndex(int elevatorId) {
		return this.elevatorList.indexOf(elevatorId);
	}

	public List<Integer> getElevatorList() {
		return elevatorList;
	}

	public List<Integer> getElevatorFloorList() {
		return elevatorAtFloorList;
	}

	public List<Integer> getFloorRequestedList() {
		return floorRequestedList;
	}

	public List<Integer> getNumberOccupentsList() {
		return numberOccupentsList;
	}
	
	
	
	

}