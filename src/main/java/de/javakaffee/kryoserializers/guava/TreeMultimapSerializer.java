package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.Comparator;

/**
 * A kryo {@link Serializer} for guava-libraries {@link TreeMultimap}.
 * For reading / writing, the default comparator is assumed so the multimaps are not null-safe.
 * For copying, the copy contains the same comparator instances as the original.
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
        kryo.writeClassAndObject(output, multimap.keyComparator());
        kryo.writeClassAndObject(output, multimap.valueComparator());
        writeMultimap(kryo, output, multimap);
    }

    @Override
    public TreeMultimap<Comparable, Comparable> read(Kryo kryo, Input input, Class<? extends TreeMultimap<Comparable, Comparable>> type) {
        Comparator<? super Comparable> keyComparator = (Comparator<? super Comparable>) kryo.readClassAndObject(input);
        Comparator<? super Comparable> valueComparator = (Comparator<? super Comparable>) kryo.readClassAndObject(input);
        final TreeMultimap<Comparable, Comparable> multimap = TreeMultimap.create(keyComparator, valueComparator);
        readMultimap(kryo, input, multimap);
        return multimap;
    }

    @Override
    protected Multimap createCopy(Kryo kryo, Multimap original) {
        TreeMultimap tm = (TreeMultimap) original;
        return TreeMultimap.create(tm.keyComparator(), tm.valueComparator());
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
