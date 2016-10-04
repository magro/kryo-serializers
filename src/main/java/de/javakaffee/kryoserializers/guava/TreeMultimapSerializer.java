package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.TreeMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link TreeMultimap}.
 * The default comparator is assumed so the multimaps are not null-safe.
 * This does not yet support {@link Kryo#copy(java.lang.Object)}.
 */
public class TreeMultimapSerializer extends MultimapSerializerBase<Comparable, Comparable, TreeMultimap<Comparable, Comparable>> {

    /* assumes default comparator */
    private static final boolean DOES_NOT_ACCEPT_NULL = true;

    private static final boolean IMMUTABLE = false;

    public TreeMultimapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, TreeMultimap<Comparable, Comparable> multimap) {
        writeMultimap(kryo, output, multimap);
    }

    @Override
    public TreeMultimap<Comparable, Comparable> read(Kryo kryo, Input input, Class<TreeMultimap<Comparable, Comparable>> type) {
        final TreeMultimap<Comparable, Comparable> multimap = TreeMultimap.create();
        readMultimap(kryo, input, multimap);
        return multimap;
    }

    /**
     * Creates a new {@link TreeMultimapSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final TreeMultimapSerializer serializer = new TreeMultimapSerializer();
        kryo.register(TreeMultimap.class, serializer);
    }
}
