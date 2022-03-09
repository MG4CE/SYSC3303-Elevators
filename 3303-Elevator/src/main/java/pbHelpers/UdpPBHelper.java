package pbHelpers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import elevatorCommands.ElevatorCommandProtos;
import elevatorCommands.WrapperMessage;

public abstract class UdpPBHelper {
	int sendPort, recvPort;
	DatagramSocket recvSocket;
	final int PACKET_SIZE = 1024;
	
	public UdpPBHelper(int sendPort, int recvPort) throws SocketException{
		this.sendPort = sendPort;
		this.recvPort = recvPort;
		this.recvSocket = new DatagramSocket(recvPort);
	}
	
	void sendMessage(byte[] pbMsg) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(pbMsg, pbMsg.length,
											InetAddress.getLocalHost(), this.sendPort);
		DatagramSocket sendSocket = new DatagramSocket();
		sendSocket.send(sendPacket);
		sendSocket.close();
	}
	
	protected void sendMessage(com.google.protobuf.GeneratedMessageV3 message) throws IOException {
		byte[] msgB = message.toByteArray();
		sendMessage(msgB);
	}	
	
	protected DatagramPacket receiveMessage() throws IOException {
		byte[] r = new byte[PACKET_SIZE];
		DatagramPacket rcv = new DatagramPacket(r, PACKET_SIZE);
		this.recvSocket.receive(rcv);
		return rcv;
	}

	protected void closePbSocket(){
		this.recvSocket.close();
	}

}
