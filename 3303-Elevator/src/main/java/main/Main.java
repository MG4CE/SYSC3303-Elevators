package main;

import elevatorCommands.SchedulerDispatchMessage;
import elevators.Elevator;
import protoBufHelpers.UDPHelper;

import java.io.IOException;
import java.net.InetAddress;
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
		sendMessage(msg, 24, InetAddress.getLocalHost());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Main m = new Main(23);
		m.e.setSchedulerAddress(InetAddress.getLocalHost());
		Thread elevator = new Thread(m.e);
		elevator.start();
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(5000);
		m.sendSchedulerDispatchMessage(10, 123);
		Thread.sleep(12000);
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(50000);
	}
}
