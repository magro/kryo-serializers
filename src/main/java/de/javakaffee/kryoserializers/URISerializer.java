package de.javakaffee.kryoserializers;

import java.net.URI;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class URISerializer extends Serializer<java.net.URI> {

    public URISerializer() {
        setImmutable(true);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final URI uri) {
        output.writeString(uri.toString());
    }

    @Override
    public URI read(final Kryo kryo, final Input input, final Class<URI> uriClass) {
        return URI.create(input.readString());
    }
}
