package elevators;

import java.sql.Timestamp;

public class Message {
	
	enum Direction {
		UP,
		DOWN;
	}
	
	private Timestamp time;
	private int floor;
	private Direction direction;
	private int elvatorID;
	
	public Message() {
		
	}
}
