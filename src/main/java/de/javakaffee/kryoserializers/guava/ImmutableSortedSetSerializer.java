package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Comparator;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableSortedSet}.
 */
public class ImmutableSortedSetSerializer extends Serializer<ImmutableSortedSet<Object>> {

  private static final boolean DOES_NOT_ACCEPT_NULL = false;
  private static final boolean IMMUTABLE = true;

  public ImmutableSortedSetSerializer() {
    super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
  }

  @Override
  public void write(Kryo kryo, Output output, ImmutableSortedSet<Object> object) {
    kryo.writeClassAndObject(output, object.comparator());
    output.writeInt(object.size(), true);
    for (Object elm : object) {
      kryo.writeClassAndObject(output, elm);
    }
  }

  @Override
  public ImmutableSortedSet<Object> read(Kryo kryo, Input input, Class<? extends ImmutableSortedSet<Object>> type) {
    @SuppressWarnings ("unchecked")
    ImmutableSortedSet.Builder<Object> builder = ImmutableSortedSet.orderedBy((Comparator<Object>)kryo.readClassAndObject (input));
    final int size = input.readInt(true);
    for (int i = 0; i < size; ++i) {
      builder.add(kryo.readClassAndObject(input));
    }
    return builder.build();
  }

  /**
   * Creates a new {@link ImmutableSortedSetSerializer} and registers its serializer
   * for the several ImmutableSortedSet related classes.
   *
   * @param kryo the {@link Kryo} instance to set the serializer on
   */
  public static void registerSerializers(final Kryo kryo) {

    // ImmutableSortedSet (abstract class)
    //  +- EmptyImmutableSortedSet
    //  +- RegularImmutableSortedSet
    //  +- DescendingImmutableSortedSet

    final ImmutableSortedSetSerializer serializer = new ImmutableSortedSetSerializer();

    kryo.register(ImmutableSortedSet.class, serializer);
    kryo.register(ImmutableSortedSet.of().getClass(), serializer);
    kryo.register(ImmutableSortedSet.of("").getClass(), serializer);
    kryo.register(ImmutableSortedSet.of().descendingSet ().getClass(), serializer);
  }
}
