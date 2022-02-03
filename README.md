# Iteration 1 - SYSC 3303 - The Schedulators

## Installation

Import the Iteration 1 project into your IDE.

or

Git Clone the Project

```bash
git clone https://github.com/MG4CE/SYSC3303-Elevators.git
```

The following image should show an example of the src files in the package view

![](/package_image.png)

## Usage

### Run Main Program

```java
 1. Run the main method in Main.java
 2. Right-click the Main.java file
 3. Run As -> Java Application
 2. Profit
```

### Run Java Tests

```java
 1. Right-click the test package in the package explorer
 2. Run As -> JUnit Test
```

#### Output

![](/test.png)

## Files & Description

### Command.java

- Holds Information about elevator commands that will be sent from the Floor to the Scheduler

- Commands will be created by the FloorSubsystem and put into the Scheduler's Queue to be read by the Elevator
  <br />
  <br />

### Elevator.java

- The Elevator that will poll the Queue in the scheduler to get commands, sent from the FloorSubsystem
- The elevator will simulate movement with Thread.sleep()
- Elevator will stop once a stop command is read in
  <br />
  <br />

### FloorSubsystem.java

- Reads input commands from input.txt
- Parse each line into a command object
- Add each command to the Queue held in Scheduler
- Once all commands are pushed to the Queue then a stop command will be added to the Queue
  <br />

```java
//Stop command shown below
"0:0:0.0", -1, "up", -1
```

<br />

### Main.java

- Runs the main method
- Creates

  - FloorSubsytem thread
  - Scheduler Thread
  - Elevator Thread

  <br />

### Scheduler.java

- Does nothing but hold the Queue of commands
- Elevator will read the Queue of commands
- FloorSubsystem will put commands into the Queue

<br />

### input.txt

- Holds all the test commands to be read by the Floorsubsystem

- Each line is shown in the format below

```java
 "time(HH:mm:ss.ms) floor direction selected floor"
 //Example
//Time Floor Direction selectedFloor
00:01:00.1 1 Up 1
00:02:00.1 1 Up 2
00:03:00.1 1 Up 3
```

## Team & Contributions

1. Maged - Command.java, Scheduler.java, Subsystem.java
2. Ehvan - JUnit test cases
3. Golan - Scheduler.java
4. Rodrigo - Subsystem.java, Scheduler.java
5. Kevin - Elevator.java, ReadMe
