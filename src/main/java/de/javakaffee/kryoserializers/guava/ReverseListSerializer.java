package de.javakaffee.kryoserializers.guava;

import com.google.common.collect.Lists;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link Lists.ReverseList} Serializer.
 * Treat as a {@link List} by reversing before write and after read.
 */
public abstract class ReverseListSerializer extends Serializer<List<Object>> {

    private static final CollectionSerializer serializer = new CollectionSerializer();

    @SuppressWarnings("unchecked")
    @Override
    public void write(Kryo kryo, Output output, List<Object> object) {
        // reverse the ReverseList to get the "forward" list, and treat as regular List.
        List forwardList = Lists.reverse(object);
        serializer.write(kryo, output, forwardList);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> copy(Kryo kryo, List<Object> original) {
        List forwardList = Lists.reverse(original);
        return Lists.reverse((List<Object>) serializer.copy(kryo, forwardList));
    }

    public static void registerSerializers(final Kryo kryo) {
        kryo.register(Lists.reverse(Lists.newLinkedList()).getClass(), forReverseList());
        kryo.register(Lists.reverse(Lists.newArrayList()).getClass(), forRandomAccessReverseList());
    }

    public static ReverseListSerializer forReverseList() {
      return new ReverseListSerializer.ReverseList();
    }

    public static ReverseListSerializer forRandomAccessReverseList() {
      return new ReverseListSerializer.RandomAccessReverseList();
    }

    /**
     * A {@link Lists.ReverseList} implementation based on a {@link LinkedList}.
     */
    private static class ReverseList extends ReverseListSerializer {

        @SuppressWarnings("unchecked")
        @Override
        public List<Object> read(Kryo kryo, Input input, Class<? extends List<Object>> type) {
            // reading a "forward" list as a LinkedList and returning the reversed list.
            List forwardList = (List) serializer.read(kryo, input, (Class) LinkedList.class);
            return Lists.reverse(forwardList);
        }
    }

    /**
     * A {@link Lists.ReverseList} implementation based on an {@link ArrayList}.
     */
    private static class RandomAccessReverseList extends ReverseListSerializer {

        @SuppressWarnings("unchecked")
        @Override
        public List<Object> read(Kryo kryo, Input input, Class<? extends List<Object>> type) {
            // reading a "forward" list as a ArrayList and returning the reversed list.
            List forwardList = (List) serializer.read(kryo, input, (Class) ArrayList.class);
            return Lists.reverse(forwardList);
        }
    }
}
