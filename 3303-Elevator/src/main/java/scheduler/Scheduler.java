package scheduler;

import elevatorCommands.Button;
import elevatorCommands.Direction;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.SchedulerDispatchMessage;
import pbHelpers.PbMessage;
import pbHelpers.UdpPBHelper;
import stateMachine.StateMachine;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class Scheduler extends UdpPBHelper implements Runnable {
    private Boolean running = true;
    private final StateMachine schedulerFSM;

    public Scheduler(int sendPort, int recvPort) throws SocketException {
        super(sendPort, recvPort);
        this.schedulerFSM = new StateMachine(null); // todo: ADD STATES
    }

    void sendSchedulerDispatchMessage(int destFloor) throws IOException {
        SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
                .setDestFloor(destFloor)
                // TODO: SET ELEVATOR ID
                //TODO ADD TIMESTAMP
                .build();
        sendMessage(msg);
    }

    @Override
    public void run() {
        while(this.running){
            try {
                DatagramPacket recvMessage = receiveMessage(); // wait for message from scheduler
                this.schedulerFSM.fireFSM(new PbMessage(recvMessage)); // update fsm
            }catch (IOException e){
                this.running = false;
                break;
            }
        }
    }
}
