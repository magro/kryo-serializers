package de.javakaffee.kryoserializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

/**
 * A kryo {@link Serializer} for fields of type {@link UnicodeBlock}, which is effectively but not
 * actually an enum.
 *
 * @author <a href="mailto:seahen123@gmail.com">Chris Hennick</a>
 */
public class UnicodeBlockSerializer extends Serializer<UnicodeBlock> {
    private static final IdentityHashMap<UnicodeBlock, String> BLOCK_NAMES
            = new IdentityHashMap<UnicodeBlock, String>();
    static {
        // Reflectively look up the instances and their names, which are in UnicodeBlock's static
        // fields (necessary since UnicodeBlock isn't an actual enum)
        for (Field field : UnicodeBlock.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    // For some reason, UnicodeBlock constants aren't already accessible, even
                    // though they're public! WTF?
                    field.setAccessible(true);
                    Object value = field.get(null);
                    if (value instanceof UnicodeBlock) {
                        BLOCK_NAMES.put((UnicodeBlock) value, field.getName());
                    }
                } catch (IllegalAccessException e) {
                    // Should never happen
                    throw new InternalError();
                }
            }
        }
    }

    public UnicodeBlockSerializer() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Kryo kryo, final Output output, final UnicodeBlock obj) {
        output.writeAscii(BLOCK_NAMES.get(obj));
    }

    /**
     * Returns {@code original}; see {@link com.esotericsoftware.kryo.serialize.EnumSerializer#copy}
     * for why we behave this way.
     */
    @Override
    public UnicodeBlock copy(final Kryo kryo, final UnicodeBlock original) {
        return original;
    }

    @Override
    public UnicodeBlock read(final Kryo kryo, final Input input,
                             final Class<? extends UnicodeBlock> unicodeBlockClass) {
        String name = input.readString();
        return (name == null) ? null : UnicodeBlock.forName(name);
    }
}
