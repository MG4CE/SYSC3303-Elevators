package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;

import message.*;

/**
 * Helper class to be extended by any class that sends/receives messages
 */
public abstract class UDPHelper {
	private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(UDPHelper.class);
	private final DatagramSocket recvSocket;
	private final int PACKET_SIZE = 1024;
	private final int TIMEOUT = 10;
	private final int MAX_NUM_RETRIES = 10;

	/**
	 * Constructor to be called with super(), will create a listen socket
	 */
	public UDPHelper() throws SocketException{
		this.recvSocket = new DatagramSocket();
	}

	/**
	 * Constructor to be called with super(), will create a listen socket bound to recvPort
	 * 
	 * @param port to listen to
	 */
	public UDPHelper(int recvPort) throws SocketException{
		this.recvSocket = new DatagramSocket(recvPort);
	}

	/**
	 * Send a raw byte array to a destination port
	 * 
	 * @param data byte array to be send via udp
	 * @param port to send to
	 */
	public void sendByteArray(byte[] data, int port, InetAddress address) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
		recvSocket.send(sendPacket);
	}
	
	/**
	 * RPC send, send data and wait for a response. Retry if useTimeout is true. 
	 * 
	 * @param sendData byte array to be send via UDP
	 * @param useTimeout select if we need to use a timeout or not
	 * @param port target port
	 * @param address target address
	 * @return response packet
	 * @throws IOException
	 */
	public DatagramPacket rpcSend(byte[] sendData, Boolean useTimeout, int port, InetAddress address) throws IOException {
		byte data_buff[] = new byte[PACKET_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data_buff, data_buff.length);
		Boolean received = false;
		int numRetries = 0;
		
		if(!useTimeout) {
			try {
				recvSocket.setSoTimeout(0);
			} catch (SocketException e) {
				e.printStackTrace();
				LOGGER.error("Failed to change port timeout!");
				return null;
			}
		} else {
			try {
				recvSocket.setSoTimeout(TIMEOUT * 1000);
			} catch (SocketException e) {
				e.printStackTrace();
				LOGGER.error("Failed to change port timeout!");
				return null;
			}
		}
		
		while (!received && numRetries < MAX_NUM_RETRIES) {
			sendByteArray(sendData, port, address);
			try {
				recvSocket.receive(receivePacket);
			} catch (SocketTimeoutException e) {
				LOGGER.warn("Host did not respond, retrying!");
				numRetries++;
				continue;
			}
			LOGGER.debug("Receiving message from " + Integer.toString(receivePacket.getPort()));
			received = true;
		}
		
		if (numRetries >= MAX_NUM_RETRIES) {
			LOGGER.error("Retry limited reached!");
			return null;
		}
		
		recvSocket.setSoTimeout(0);
		return receivePacket;
	}
	
	/**
	 * RPC send a message of type protobuf message, will wrap message in WrapperMessage first
	 * 
	 * @param message protobuf message
	 * @param port target port
	 * @param address target address
	 * @return response packet
	 * @throws IOException
	 */
	public DatagramPacket rpcSendMessage(com.google.protobuf.GeneratedMessageV3 message, int port, InetAddress address) throws IOException {
		//LOGGER.info("Sending Protobuf of type " + message.getClass().getName());
		WrapperMessage msg = createWrapperMessage(message);
		return rpcSend(msg.toByteArray(), true, port, address);
	}	
	
	/**
	 * Send a message of type protobuf message (Any of defined messages) Will wrap message in WrapperMessage first
	 * 
	 * @param Protobuf message to send
	 * @param port to send message to
	 */
	public void sendMessage(com.google.protobuf.GeneratedMessageV3 message, int port, InetAddress address) throws IOException {
		LOGGER.debug("Sending Protobuf of type " + message.getClass().getName());
		WrapperMessage msg = createWrapperMessage(message);
		sendByteArray(msg.toByteArray(), port, address);
	}	
	
	public static WrapperMessage createWrapperMessage(com.google.protobuf.GeneratedMessageV3 message) throws IOException {
		WrapperMessage.Builder msgBldr = WrapperMessage.newBuilder();		
		// find type of message
		if(message instanceof ElevatorRequestMessage){
			msgBldr.setElevatorRequest((ElevatorRequestMessage) message);
		}else if(message instanceof SchedulerDispatchMessage){
			msgBldr.setSchedulerDispatch((SchedulerDispatchMessage) message);
		}else if(message instanceof ElevatorArrivedMessage){
			msgBldr.setElevatorArrived((ElevatorArrivedMessage) message);
		}else if(message instanceof ElevatorDepartureMessage){
			msgBldr.setElevatorDeparture((ElevatorDepartureMessage) message);
		}else if(message instanceof FloorSensorMessage){
			msgBldr.setFloorSensor((FloorSensorMessage) message);
		}else if(message instanceof LampMessage){
			msgBldr.setLampMessage((LampMessage) message);
		}else if(message instanceof ElevatorRegisterMessage) {
			msgBldr.setRegisterMessage((ElevatorRegisterMessage)message);
		}else if(message instanceof FaultMessage) {
			msgBldr.setFaultMessage((FaultMessage)message);
		}else if(message instanceof SimulateFaultMessage) {
			msgBldr.setSimFaultMessage((SimulateFaultMessage)message);
		}else if(message instanceof WrapperMessage){
			return (WrapperMessage) message; // mostly for debugging/testing
		}
		else {
			throw new IOException("Failed to cast proto msg to correct type");
		}
		// build and return message
		return msgBldr.build();
	}
	
	/**
	 * Blocking call to receive a message, returns received packet
	 * 
	 * @return packet received from listen socket
	 */
	public DatagramPacket receiveMessage() throws IOException {
		byte[] r = new byte[PACKET_SIZE];
		DatagramPacket rcv = new DatagramPacket(r, PACKET_SIZE);
		this.recvSocket.receive(rcv);
		LOGGER.debug("Receiving message from " + Integer.toString(rcv.getPort()));
		return rcv;
	}

	/**
	 * Close socket
	 */
	public void closePbSocket(){
		this.recvSocket.close();
	}
}
