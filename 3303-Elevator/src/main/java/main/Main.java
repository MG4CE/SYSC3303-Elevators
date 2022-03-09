package main;

import elevatorCommands.SchedulerDispatchMessage;
import elevators.Elevator;
import pbHelpers.UdpPBHelper;

import java.io.IOException;
import java.net.SocketException;

public class Main extends UdpPBHelper {
	Elevator e = new Elevator(24, 23);
	public Main(int sendPort, int recvPort) throws SocketException {
		super(sendPort, recvPort);
	}

	void sendSchedulerDispatchMessage(int destFloor, int elevatorID) throws IOException {
		SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
				.setDestFloor(destFloor)
				.setElevatorID(elevatorID)
				// TODO: SET ELEVATOR ID
				//TODO ADD TIMESTAMP
				.build();
		sendMessage(msg);
	}


	public static void main(String[] args) throws IOException, InterruptedException {
		Main m = new Main(23, 24);
		Thread elevator = new Thread(m.e);
		elevator.start();
		m.sendSchedulerDispatchMessage(10, 123);
		Thread.sleep(12000);
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(50000);
	}
}
