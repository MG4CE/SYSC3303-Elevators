package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.Command;
import stateMachines.Scheduler;
import floors.FloorSubsystem;
import stateMachines.Scheduler;

class FloorSubsystemTest {
	//Initializing test parameters 
	private FloorSubsystem testFloorSubsystem;
	private Scheduler testScheduler;
	private String TEST_FILE = "data/input.txt";
	private String TEST_INVALID_FILE = "data/invalidCommands.txt";
	
	@BeforeEach
	void init() {
		testScheduler = new Scheduler();
		testFloorSubsystem = new FloorSubsystem(testScheduler, TEST_FILE);
	}
	
	@Test
	void testReadFile() {
		ArrayList<Command> commands = testFloorSubsystem.readCommandsFile(TEST_FILE);
		for(int i = 0; i < commands.size(); i++) {
			assertNotNull(commands.get(i));
		}
	}
	
	@Test
	void testReadFileInvalid() {
		assertNull(testFloorSubsystem.readCommandsFile(TEST_INVALID_FILE));
	}

}
