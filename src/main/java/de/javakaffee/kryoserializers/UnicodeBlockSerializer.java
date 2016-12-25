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
 * Created by chris on 12/25/2016.
 */

public class UnicodeBlockSerializer extends Serializer<UnicodeBlock> {
    private static final IdentityHashMap<UnicodeBlock, String> BLOCK_NAMES
            = new IdentityHashMap<UnicodeBlock, String>();

    static {
        for (Field field : UnicodeBlock.class.getFields()) {
            if (field.isAccessible() && Modifier.isStatic(field.getModifiers())) {
                try {
                    Object value = field.get(null);
                    if (value instanceof UnicodeBlock) {
                        BLOCK_NAMES.put((UnicodeBlock) value, field.getName());
                    }
                } catch (IllegalAccessException e) {
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

    @Override
    public UnicodeBlock read(final Kryo kryo, final Input input,
                             final Class<UnicodeBlock> unicodeBlockClass) {
        return UnicodeBlock.forName(input.readString());
    }
}
