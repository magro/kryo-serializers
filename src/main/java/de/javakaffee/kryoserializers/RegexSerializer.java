package de.javakaffee.kryoserializers;

import java.util.regex.Pattern;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Kryo {@link Serializer} for regex {@link Pattern}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 * @author serverperformance
 */
public class RegexSerializer extends Serializer<Pattern> {

    public RegexSerializer() {
        setImmutable(true);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Pattern pattern) {
        output.writeString(pattern.pattern());
        output.writeInt(pattern.flags(), true);
    }

    @Override
    public Pattern read(final Kryo kryo, final Input input, final Class<Pattern> patternClass) {
        String regex = input.readString();
        int flags = input.readInt(true);
        return Pattern.compile(regex, flags);
    }
}
