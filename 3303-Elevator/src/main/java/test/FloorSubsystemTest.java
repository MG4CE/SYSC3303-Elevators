package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevators.IdleState;
import main.Main;


/**
 * Testing Elevator Movements
 * @author kevin
 *
 */
class FloorSubsystemTest {
    

    /**
     * Setup main
     */
    @BeforeAll
    public static void init() throws SocketException, UnknownHostException {
//        main = new Main(23);
//        elevator = new Thread(main.e);
//        elevator.start();
    }

    /**
     * Sleep 2 seconds before each test
     * @throws InterruptedException
     */
    @BeforeEach
    void sleep() throws InterruptedException {
        Thread.sleep(2000);
    }

    /**
     * Test
     * @throws InterruptedException
     */
    @Test
    void testIntialState() throws InterruptedException {
        Thread.sleep(200);
        assertTrue(main.e.getEFSM().getCurrentState() instanceof IdleState);
    }

    /**
     * Tear down
     */
    @AfterAll
    public static void teardown() {
        elevator.interrupt();
        System.out.println("teardown");
    }

}
