package main;

import elevatorCommands.ElevatorRegisterMessage;
import elevatorCommands.SchedulerDispatchMessage;
import elevators.Elevator;
import floorSubsystem.FloorSubsystem;
import protoBufHelpers.UDPHelper;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main extends UDPHelper {
	private Elevator e;
	public Main(int recvPort) throws SocketException {
		super(recvPort);
		try {
			e = new Elevator(23, InetAddress.getLocalHost(), 24);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
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
	
	private void sendElevatorRegisterMessage(int elevatorID) throws IOException {
		ElevatorRegisterMessage msg = ElevatorRegisterMessage.newBuilder()
				.setElevatorID(elevatorID)
				.build();

		sendMessage(msg, 24, InetAddress.getLocalHost());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Main m = new Main(23);
		Thread elevator = new Thread(m.e);
		elevator.start();
		Thread.sleep(1000);
		m.sendElevatorRegisterMessage(1);
		Thread.sleep(2000);
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(5000);
		m.sendSchedulerDispatchMessage(10, 123);
		Thread.sleep(12000);
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(50000);
	}
}
