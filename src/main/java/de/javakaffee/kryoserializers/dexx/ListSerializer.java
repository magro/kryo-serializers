package de.javakaffee.kryoserializers.dexx;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.github.andrewoma.dexx.collection.IndexedLists;
import com.github.andrewoma.dexx.collection.List;

/**
 * A kryo {@link Serializer} for dexx {@link List}
 */
public class ListSerializer extends Serializer<List> {

    private static final boolean DOES_NOT_ACCEPT_NULL = true;
    private static final boolean IMMUTABLE = true;

    public ListSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, List object) {
        output.writeInt(object.size(), true);
        for (Object elm : object) {
            kryo.writeClassAndObject(output, elm);
        }
    }

    @Override
    public List<Object> read(Kryo kryo, Input input, Class<? extends List> aClass) {
        final int size = input.readInt(true);
        final Object[] list = new Object[size];
        for (int i = 0; i < size; ++i) {
            list[i] = kryo.readClassAndObject(input);
        }
        return IndexedLists.copyOf(list);
    }

    /**
     * Creates a new {@link  ImmutableListSerializer} and registers its serializer
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        final ListSerializer serializer = new ListSerializer();

        kryo.register(List.class, serializer);

        // Note:
        //  Only registering above is good enough for serializing/deserializing.
        //  but if using Kryo#copy, following is required.

        kryo.register(IndexedLists.of().getClass(), serializer);
        kryo.register(IndexedLists.of(1).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5,6).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5,6,7).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5,6,7,8).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5,6,7,8,9).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5,6,7,8,10).getClass(), serializer);
        kryo.register(IndexedLists.of(1,2,3,4,5,6,7,8,10,11).getClass(), serializer);

    }
}
