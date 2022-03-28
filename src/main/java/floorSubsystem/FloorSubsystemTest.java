package floorSubsystem;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Testing Floor Subsystem input
 * @author Rodrigo Fierro
 *
 */
class FloorSubsystemTest {
    static Thread floorsubsystem;
    static int schedulerPort;
    static InetAddress schedulerAddress;
    static String commandFile;

    /**
     * Setup main and floor subsystem
     */
    @BeforeAll
    public static void init() throws SocketException, UnknownHostException {
        schedulerPort = 6666;
        schedulerAddress = InetAddress.getLocalHost();
        commandFile = "documents/input/input.txt";
    }

    /**
     * Sleep 2 second before each test
     * @throws InterruptedException
     */
    @BeforeEach
    void sleep() throws InterruptedException {
        Thread.sleep(2000);
    }



// ==============================================
    /**
     * Send
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testHardFaultMessage() throws IOException, InterruptedException {
        String commandFile = "documents/input/input.txt";
        Thread floorThread = new Thread (new FloorSubsystem(schedulerPort,commandFile, schedulerAddress));
        floorThread.start();
    }

    /**
     * Tests that floor subsystem is done sending messages
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testTotalExecutionTime() throws IOException, InterruptedException {
        FloorSubsystem floorSubsystem = new FloorSubsystem(schedulerPort,commandFile, schedulerAddress);
        Thread floorThread = new Thread (floorSubsystem);

        long startTime = System.currentTimeMillis();
        floorThread.start();
        while(true){
            Thread.sleep(100);
            if (floorSubsystem.isInputFileParsed()){
                break;
            }
        };
        long endTime = System.currentTimeMillis();

        long difference = endTime-startTime;
        System.out.println("End Time: " + endTime);
        System.out.println("Execution time (ms): " + difference);

    }

// ==============================================
    /**
     * Tear down
     */
    @AfterAll
    public static void teardown() {
        System.out.println("teardown");
    }

}