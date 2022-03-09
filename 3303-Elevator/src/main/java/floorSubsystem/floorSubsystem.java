package floorSubsystem;

import elevators.Elevator;
import pbHelpers.PbMessage;
import pbHelpers.UdpPBHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class floorSubsystem extends UdpPBHelper{
    private int NUM_LINES = 8; // rodrigo do this

    floorSubsystem(Elevator elevator) throws SocketException {
        super(0,0);

    }

    void start(){
        for(int i = 0; i < NUM_LINES; i++){
        }
    }
}
