// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: elevator.proto

package message;

/**
 * Protobuf enum {@code elevatorCommands.Button}
 */
public enum Button
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>INTERIOR = 0;</code>
   */
  INTERIOR(0),
  /**
   * <code>EXTERIOR = 1;</code>
   */
  EXTERIOR(1),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>INTERIOR = 0;</code>
   */
  public static final int INTERIOR_VALUE = 0;
  /**
   * <code>EXTERIOR = 1;</code>
   */
  public static final int EXTERIOR_VALUE = 1;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static Button valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static Button forNumber(int value) {
    switch (value) {
      case 0: return INTERIOR;
      case 1: return EXTERIOR;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<Button>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      Button> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<Button>() {
          public Button findValueByNumber(int number) {
            return Button.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalStateException(
          "Can't get the descriptor of an unrecognized enum value.");
    }
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return message.ElevatorCommandProtos.getDescriptor().getEnumTypes().get(3);
  }

  private static final Button[] VALUES = values();

  public static Button valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private Button(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:elevatorCommands.Button)
}

