package pbHelpers;

import java.net.DatagramPacket;
import java.util.Arrays;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.FloorSensorMessage;
import elevatorCommands.LampMessage;
import elevatorCommands.SchedulerDispatchMessage;
import elevatorCommands.WrapperMessage;
	

public class PbMessage {
	WrapperMessage wrapper;
	com.google.protobuf.GeneratedMessageV3 message;
	
	public PbMessage(DatagramPacket message) throws InvalidProtocolBufferException{
		byte[] rawPBArray = cpyDatagramArr(message);
		this.wrapper = getWrapper(rawPBArray); // unpack raw message
		this.unpackMessage(wrapper); // get internal message
	}
	
	// will not be able to parse without this!
	private byte[] cpyDatagramArr(DatagramPacket p){
		return Arrays.copyOfRange(p.getData(), 0, p.getLength());
	}
	
	private WrapperMessage getWrapper(byte[] rawData) throws InvalidProtocolBufferException {
		WrapperMessage w = WrapperMessage.parseFrom(rawData);
		return w;
	}
	
	private void unpackMessage(WrapperMessage msg) {
		switch(msg.getMsgCase()){
			case ELEVATORARRIVED:
				this.message = msg.getElevatorArrived();
				break;
			case ELEVATORDEPARTURE:
				this.message = msg.getElevatorDeparture();
				break;
			case ELEVATORREQUEST:
				this.message = msg.getElevatorRequest();
				break;
			case FLOORSENSOR:
				this.message = msg.getFloorSensor();
				break;
			case LAMPMESSAGE:
				this.message = msg.getLampMessage();
				break;
			case SCHEDULERDISPATCH:
				this.message = msg.getSchedulerDispatch();
				break;				
			default:
				this.message = null;
				break;
		}
	}
	
	public Boolean isElevatorRequestMessage() {
		return this.message instanceof ElevatorRequestMessage;
	}
	
	public Boolean isSchedulerDispatchMessage() {
		return this.message instanceof SchedulerDispatchMessage;
	}

	public Boolean isElevatorArrivedMessage() {
		return this.message instanceof ElevatorArrivedMessage;
	}
	
	public Boolean isElevatorDepartureMessage() {
		return this.message instanceof ElevatorDepartureMessage;
	}
	
	public Boolean isFloorSensorMessage() {
		return this.message instanceof FloorSensorMessage;
	}
	
	public Boolean isLampMessage() {
		return this.message instanceof LampMessage;
	}
 
	
	//TODO: ADD ERROR CHECKING TO CASTS!
	
	public ElevatorRequestMessage toElevatorRequestMessage() {
		return (ElevatorRequestMessage)(this.message);
	}
	
	public SchedulerDispatchMessage toSchedulerDispatchMessage() {
		return (SchedulerDispatchMessage)(this.message);
	}
	
	public ElevatorArrivedMessage toElevatorArrivedMessage() {
		return (ElevatorArrivedMessage)(this.message);
	}
	
	public ElevatorDepartureMessage toElevatorDepartureMessage() {
		return (ElevatorDepartureMessage)(this.message);
	}
	
	public FloorSensorMessage toFloorSensorMessage() {
		return (FloorSensorMessage)(this.message);
	}
	
	public LampMessage toLampMessage() {
		return (LampMessage)(this.message);
	}
}
