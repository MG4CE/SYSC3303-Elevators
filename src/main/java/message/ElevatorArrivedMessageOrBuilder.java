// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: elevator.proto

package message;

public interface ElevatorArrivedMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:elevatorCommands.ElevatorArrivedMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint32 elevatorID = 1;</code>
   * @return The elevatorID.
   */
  int getElevatorID();

  /**
   * <code>uint32 floor = 2;</code>
   * @return The floor.
   */
  int getFloor();

  /**
   * <code>string timeStamp = 3;</code>
   * @return The timeStamp.
   */
  java.lang.String getTimeStamp();
  /**
   * <code>string timeStamp = 3;</code>
   * @return The bytes for timeStamp.
   */
  com.google.protobuf.ByteString
      getTimeStampBytes();

  /**
   * <code>uint32 requestID = 4;</code>
   * @return The requestID.
   */
  int getRequestID();
}
