package main;

import elevatorCommands.SchedulerDispatchMessage;
import elevators.Elevator;
import floorSubsystem.FloorSubsystem;
import protoBufHelpers.UDPHelper;

import java.io.IOException;
import java.net.SocketException;

public class Main extends UDPHelper {
	Elevator e = new Elevator(23, 24);
	public Main(int recvPort) throws SocketException {
		super(recvPort);
	}

	void sendSchedulerDispatchMessage(int destFloor, int elevatorID) throws IOException {
		SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
				.setDestFloor(destFloor)
				.setElevatorID(elevatorID)
				// TODO: SET ELEVATOR ID
				//TODO ADD TIMESTAMP
				.build();
		sendMessage(msg, 24);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		Main m = new Main(23);
//		Thread elevator = new Thread(m.e);
//		elevator.start();
		String inputFile = "src\\main\\java\\input\\input.txt";

		Thread f = new Thread(new FloorSubsystem(2323, inputFile));
		f.start();

//		m.sendSchedulerDispatchMessage(0, 123);
//		Thread.sleep(5000);
//		m.sendSchedulerDispatchMessage(10, 123);
//		Thread.sleep(12000);
//		m.sendSchedulerDispatchMessage(0, 123);
//		Thread.sleep(50000);
	}
}
