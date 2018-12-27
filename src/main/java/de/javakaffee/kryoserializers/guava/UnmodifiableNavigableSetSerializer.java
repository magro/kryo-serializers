package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableSortedSet}.
 */
public class UnmodifiableNavigableSetSerializer extends Serializer<NavigableSet<?>> {

    Field delegate;

    public UnmodifiableNavigableSetSerializer() {
        // Do not allow nulls
        super(false);
        try {
            Class<?> clazz = Class.forName(Sets.class.getCanonicalName() + "$UnmodifiableNavigableSet");
            delegate = clazz.getDeclaredField("delegate");
            delegate.setAccessible(true);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Issues reflectively writing UnmodifiableNavigableSet", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Issues reflectively writing UnmodifiableNavigableSet", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Issues reflectively writing UnmodifiableNavigableSet", e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Issues reflectively writing UnmodifiableNavigableSet", e);
        }
    }

    @VisibleForTesting
    protected Object getDelegateFromUnmodifiableNavigableSet(NavigableSet<?> object) {
        try {
            return delegate.get(object);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Issues reflectively writing UnmodifiableNavigableSet", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Issues reflectively writing UnmodifiableNavigableSet", e);
        }
    }

    @Override
    public void write(Kryo kryo, Output output, NavigableSet<?> object) {
        // We want to preserve the underlying delegate class, so we need to reflectively get it and write it directly via kryo
        kryo.writeClassAndObject(output, getDelegateFromUnmodifiableNavigableSet(object));
    }

    @Override
    public NavigableSet<?> read(Kryo kryo, Input input, Class<? extends NavigableSet<?>> type) {
        return Sets.unmodifiableNavigableSet((NavigableSet<?>) kryo.readClassAndObject(input));
    }

    @Override
    public NavigableSet<?> copy(Kryo kryo, NavigableSet<?> original) {
        return Sets.unmodifiableNavigableSet((NavigableSet<?>) kryo.copy(getDelegateFromUnmodifiableNavigableSet(original)));
    }

    /**
     * Creates a new {@link UnmodifiableNavigableSetSerializer} and registers its serializer
     * for the UnmodifiableNavigableSetSerializer related class.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        // UnmodifiableNavigableSetSerializer (private class)

        final UnmodifiableNavigableSetSerializer serializer = new UnmodifiableNavigableSetSerializer();

        kryo.register(Sets.unmodifiableNavigableSet(new TreeSet<Object>()).getClass(), serializer);
    }
}
