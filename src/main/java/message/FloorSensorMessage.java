// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: elevator.proto

package message;

/**
 * <pre>
 * Tell scheduler when elevator passes a floor
 * </pre>
 *
 * Protobuf type {@code elevatorCommands.FloorSensorMessage}
 */
public final class FloorSensorMessage extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:elevatorCommands.FloorSensorMessage)
    FloorSensorMessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use FloorSensorMessage.newBuilder() to construct.
  private FloorSensorMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private FloorSensorMessage() {
    timeStamp_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new FloorSensorMessage();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private FloorSensorMessage(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            elevatorID_ = input.readUInt32();
            break;
          }
          case 16: {

            floor_ = input.readUInt32();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            timeStamp_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return message.ElevatorCommandProtos.internal_static_elevatorCommands_FloorSensorMessage_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return message.ElevatorCommandProtos.internal_static_elevatorCommands_FloorSensorMessage_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            message.FloorSensorMessage.class, message.FloorSensorMessage.Builder.class);
  }

  public static final int ELEVATORID_FIELD_NUMBER = 1;
  private int elevatorID_;
  /**
   * <code>uint32 elevatorID = 1;</code>
   * @return The elevatorID.
   */
  @java.lang.Override
  public int getElevatorID() {
    return elevatorID_;
  }

  public static final int FLOOR_FIELD_NUMBER = 2;
  private int floor_;
  /**
   * <code>uint32 floor = 2;</code>
   * @return The floor.
   */
  @java.lang.Override
  public int getFloor() {
    return floor_;
  }

  public static final int TIMESTAMP_FIELD_NUMBER = 3;
  private volatile java.lang.Object timeStamp_;
  /**
   * <code>string timeStamp = 3;</code>
   * @return The timeStamp.
   */
  @java.lang.Override
  public java.lang.String getTimeStamp() {
    java.lang.Object ref = timeStamp_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      timeStamp_ = s;
      return s;
    }
  }
  /**
   * <code>string timeStamp = 3;</code>
   * @return The bytes for timeStamp.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getTimeStampBytes() {
    java.lang.Object ref = timeStamp_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      timeStamp_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (elevatorID_ != 0) {
      output.writeUInt32(1, elevatorID_);
    }
    if (floor_ != 0) {
      output.writeUInt32(2, floor_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(timeStamp_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, timeStamp_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (elevatorID_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, elevatorID_);
    }
    if (floor_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(2, floor_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(timeStamp_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, timeStamp_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof message.FloorSensorMessage)) {
      return super.equals(obj);
    }
    message.FloorSensorMessage other = (message.FloorSensorMessage) obj;

    if (getElevatorID()
        != other.getElevatorID()) return false;
    if (getFloor()
        != other.getFloor()) return false;
    if (!getTimeStamp()
        .equals(other.getTimeStamp())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + ELEVATORID_FIELD_NUMBER;
    hash = (53 * hash) + getElevatorID();
    hash = (37 * hash) + FLOOR_FIELD_NUMBER;
    hash = (53 * hash) + getFloor();
    hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
    hash = (53 * hash) + getTimeStamp().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static message.FloorSensorMessage parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static message.FloorSensorMessage parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static message.FloorSensorMessage parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static message.FloorSensorMessage parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static message.FloorSensorMessage parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static message.FloorSensorMessage parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static message.FloorSensorMessage parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static message.FloorSensorMessage parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static message.FloorSensorMessage parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static message.FloorSensorMessage parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static message.FloorSensorMessage parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static message.FloorSensorMessage parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(message.FloorSensorMessage prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * Tell scheduler when elevator passes a floor
   * </pre>
   *
   * Protobuf type {@code elevatorCommands.FloorSensorMessage}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:elevatorCommands.FloorSensorMessage)
      message.FloorSensorMessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return message.ElevatorCommandProtos.internal_static_elevatorCommands_FloorSensorMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return message.ElevatorCommandProtos.internal_static_elevatorCommands_FloorSensorMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              message.FloorSensorMessage.class, message.FloorSensorMessage.Builder.class);
    }

    // Construct using elevatorCommands.FloorSensorMessage.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      elevatorID_ = 0;

      floor_ = 0;

      timeStamp_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return message.ElevatorCommandProtos.internal_static_elevatorCommands_FloorSensorMessage_descriptor;
    }

    @java.lang.Override
    public message.FloorSensorMessage getDefaultInstanceForType() {
      return message.FloorSensorMessage.getDefaultInstance();
    }

    @java.lang.Override
    public message.FloorSensorMessage build() {
      message.FloorSensorMessage result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public message.FloorSensorMessage buildPartial() {
      message.FloorSensorMessage result = new message.FloorSensorMessage(this);
      result.elevatorID_ = elevatorID_;
      result.floor_ = floor_;
      result.timeStamp_ = timeStamp_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof message.FloorSensorMessage) {
        return mergeFrom((message.FloorSensorMessage)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(message.FloorSensorMessage other) {
      if (other == message.FloorSensorMessage.getDefaultInstance()) return this;
      if (other.getElevatorID() != 0) {
        setElevatorID(other.getElevatorID());
      }
      if (other.getFloor() != 0) {
        setFloor(other.getFloor());
      }
      if (!other.getTimeStamp().isEmpty()) {
        timeStamp_ = other.timeStamp_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      message.FloorSensorMessage parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (message.FloorSensorMessage) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int elevatorID_ ;
    /**
     * <code>uint32 elevatorID = 1;</code>
     * @return The elevatorID.
     */
    @java.lang.Override
    public int getElevatorID() {
      return elevatorID_;
    }
    /**
     * <code>uint32 elevatorID = 1;</code>
     * @param value The elevatorID to set.
     * @return This builder for chaining.
     */
    public Builder setElevatorID(int value) {
      
      elevatorID_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint32 elevatorID = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearElevatorID() {
      
      elevatorID_ = 0;
      onChanged();
      return this;
    }

    private int floor_ ;
    /**
     * <code>uint32 floor = 2;</code>
     * @return The floor.
     */
    @java.lang.Override
    public int getFloor() {
      return floor_;
    }
    /**
     * <code>uint32 floor = 2;</code>
     * @param value The floor to set.
     * @return This builder for chaining.
     */
    public Builder setFloor(int value) {
      
      floor_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint32 floor = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearFloor() {
      
      floor_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object timeStamp_ = "";
    /**
     * <code>string timeStamp = 3;</code>
     * @return The timeStamp.
     */
    public java.lang.String getTimeStamp() {
      java.lang.Object ref = timeStamp_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        timeStamp_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string timeStamp = 3;</code>
     * @return The bytes for timeStamp.
     */
    public com.google.protobuf.ByteString
        getTimeStampBytes() {
      java.lang.Object ref = timeStamp_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        timeStamp_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string timeStamp = 3;</code>
     * @param value The timeStamp to set.
     * @return This builder for chaining.
     */
    public Builder setTimeStamp(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      timeStamp_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string timeStamp = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearTimeStamp() {
      
      timeStamp_ = getDefaultInstance().getTimeStamp();
      onChanged();
      return this;
    }
    /**
     * <code>string timeStamp = 3;</code>
     * @param value The bytes for timeStamp to set.
     * @return This builder for chaining.
     */
    public Builder setTimeStampBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      timeStamp_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:elevatorCommands.FloorSensorMessage)
  }

  // @@protoc_insertion_point(class_scope:elevatorCommands.FloorSensorMessage)
  private static final message.FloorSensorMessage DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new message.FloorSensorMessage();
  }

  public static message.FloorSensorMessage getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<FloorSensorMessage>
      PARSER = new com.google.protobuf.AbstractParser<FloorSensorMessage>() {
    @java.lang.Override
    public FloorSensorMessage parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new FloorSensorMessage(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<FloorSensorMessage> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<FloorSensorMessage> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public message.FloorSensorMessage getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
