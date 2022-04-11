package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import communication.UDPHelper;

/**
 * This is just a listening udp socket on the scheduler process to listen for messages from the floorSubSystem
 * The floor subsystem sends messages to this to be used for later
 * @author kevin
 *
 */
public class TimeMessageServer extends UDPHelper implements Runnable {

	private SchedulerTCPServer schedServer;
	public TimeMessageServer(SchedulerTCPServer schedServer) throws SocketException {
		super(119);
		this.schedServer = schedServer;
	}

	@Override
	public void run() {
		while(true) {
		try {
			//Listen for packet
			DatagramPacket receiveMessage = this.receiveMessage();
			this.schedServer.addMessagesToBeanArray(convertByteToString(receiveMessage.getData()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}
	/**
	 * Method to convert byte array to string and trim all null chars
	 * @param value
	 * @return
	 */
	private String convertByteToString(byte[] value) {
		String s = new String(value).replace("\u0000", "");
		System.out.println(s);
		return s;
	}

}
