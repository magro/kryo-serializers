package de.javakaffee.kryoserializers;

import java.util.regex.Pattern;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class RegexSerializer extends Serializer<Pattern> {

    public RegexSerializer() {
        setImmutable(true);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Pattern pattern) {
        output.writeString(pattern.pattern());
    }

    @Override
    public Pattern read(final Kryo kryo, final Input input, final Class<Pattern> patternClass) {
        return Pattern.compile(input.readString());
    }
}
